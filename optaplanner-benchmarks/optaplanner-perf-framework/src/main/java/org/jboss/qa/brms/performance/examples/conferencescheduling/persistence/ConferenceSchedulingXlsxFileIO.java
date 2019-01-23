/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.qa.brms.performance.examples.conferencescheduling.persistence;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFCell;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.qa.brms.performance.examples.conferencescheduling.ConferenceScheduling;
import org.jboss.qa.brms.performance.examples.common.persistence.AbstractXlsxSolutionFileIO;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.ConferenceConstraintConfiguration;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.ConferenceSolution;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.Room;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.Speaker;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.Talk;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.TalkType;
import org.jboss.qa.brms.performance.examples.conferencescheduling.domain.Timeslot;

import static java.util.stream.Collectors.*;
import static org.jboss.qa.brms.performance.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.*;

public class ConferenceSchedulingXlsxFileIO extends AbstractXlsxSolutionFileIO<ConferenceSolution> {

    private static final String ROOM_UNAVAILABLE_TIMESLOT_DESCRIPTION = "Penalty per talk with an unavailable room in its timeslot, per minute";
    private static final String ROOM_CONFLICT_DESCRIPTION = "Penalty per 2 talks in the same room and overlapping timeslots, per overlapping minute";
    private static final String SPEAKER_UNAVAILABLE_TIMESLOT_DESCRIPTION = "Penalty per talk with an unavailable speaker in its timeslot, per minute";
    private static final String SPEAKER_CONFLICT_DESCRIPTION = "Penalty per 2 talks with the same speaker and overlapping timeslots, per overlapping minute";
    private static final String TALK_PREREQUISITE_TALKS_DESCRIPTION = "Penalty per prerequisite talk of a talk that doesn't end before the second talk starts, per minute of either talk";
    private static final String TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS_DESCRIPTION = "Penalty per common mutually exclusive talks tag of 2 talks with overlapping timeslots, per overlapping minute";
    private static final String CONSECUTIVE_TALKS_PAUSE_DESCRIPTION = "Penalty per 2 consecutive talks for the same speaker with a pause less than the minimum pause, per minute of either talk";
    private static final String CROWD_CONTROL_DESCRIPTION = "Penalty per talk with a non-zero crowd control risk that are not in paired with exactly one other such talk, per minute of either talk";

    private static final String SPEAKER_REQUIRED_TIMESLOT_TAGS_DESCRIPTION = "Penalty per missing required tag in a talk's timeslot, per minute";
    private static final String SPEAKER_PROHIBITED_TIMESLOT_TAGS_DESCRIPTION = "Penalty per prohibited tag in a talk's timeslot, per minute";
    private static final String TALK_REQUIRED_TIMESLOT_TAGS_DESCRIPTION = "Penalty per missing required tag in a talk's timeslot, per minute";
    private static final String TALK_PROHIBITED_TIMESLOT_TAGS_DESCRIPTION = "Penalty per prohibited tag in a talk's timeslot, per minute";
    private static final String SPEAKER_REQUIRED_ROOM_TAGS_DESCRIPTION = "Penalty per missing required tag in a talk's room, per minute";
    private static final String SPEAKER_PROHIBITED_ROOM_TAGS_DESCRIPTION = "Penalty per prohibited tag in a talk's room, per minute";
    private static final String TALK_REQUIRED_ROOM_TAGS_DESCRIPTION = "Penalty per missing required tag in a talk's room, per minute";
    private static final String TALK_PROHIBITED_ROOM_TAGS_DESCRIPTION = "Penalty per prohibited tag in a talk's room, per minute";

    private static final String PUBLISHED_TIMESLOT_DESCRIPTION = "Penalty per published talk with a different timeslot than its published timeslot, per match";

    private static final String PUBLISHED_ROOM_DESCRIPTION = "Penalty per published talk with a different room than its published room, per match";
    private static final String THEME_TRACK_CONFLICT_DESCRIPTION = "Penalty per common theme track of 2 talks with overlapping timeslots, per overlapping minute";
    private static final String THEME_TRACK_ROOM_STABILITY_DESCRIPTION = "Penalty per common theme track of 2 talks in a different room on the same day, per minute of either talk";
    private static final String SECTOR_CONFLICT_DESCRIPTION = "Penalty per common sector of 2 talks with overlapping timeslots, per overlapping minute";
    private static final String AUDIENCE_TYPE_DIVERSITY_DESCRIPTION = "Reward per 2 talks with a different audience type and the same timeslot, per (overlapping) minute";
    private static final String AUDIENCE_TYPE_THEME_TRACK_CONFLICT_DESCRIPTION = "Penalty per 2 talks with a common audience type, a common theme track and overlapping timeslotst, per overlapping minute";
    private static final String AUDIENCE_LEVEL_DIVERSITY_DESCRIPTION = "Reward per 2 talks with a different audience level and the same timeslot, per (overlapping) minute";
    private static final String CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION_DESCRIPTION = "Penalty per common content of 2 talks with a different audience level for which the easier talk isn't scheduled earlier than the other talk, per minute of either talk";
    private static final String CONTENT_CONFLICT_DESCRIPTION = "Penalty per common content of 2 talks with overlapping timeslots, per overlapping minute";
    private static final String LANGUAGE_DIVERSITY_DESCRIPTION = "Reward per 2 talks with a different language and the the same timeslot, per (overlapping) minute";
    private static final String SAME_DAY_TALKS_DESCRIPTION = "Penalty per common content or theme track of 2 talks with a different day, per minute of either talk";
    private static final String POPULAR_TALKS_DESCRIPTION = "Penalty per 2 talks where the less popular one (has lower favorite count) is assigned a larger room than the more popular talk";

    private static final String SPEAKER_PREFERRED_TIMESLOT_TAGS_DESCRIPTION = "Penalty per missing preferred tag in a talk's timeslot, per minute";
    private static final String SPEAKER_UNDESIRED_TIMESLOT_TAGS_DESCRIPTION = "Penalty per undesired tag in a talk's timeslot, per minute";
    private static final String TALK_PREFERRED_TIMESLOT_TAGS_DESCRIPTION = "Penalty per missing preferred tag in a talk's timeslot, per minute";
    private static final String TALK_UNDESIRED_TIMESLOT_TAGS_DESCRIPTION = "Penalty per undesired tag in a talk's timeslot, per minute";
    private static final String SPEAKER_PREFERRED_ROOM_TAGS_DESCRIPTION = "Penalty per missing preferred tag in a talk's room, per minute";
    private static final String SPEAKER_UNDESIRED_ROOM_TAGS_DESCRIPTION = "Penalty per undesired tag in a talk's room, per minute";
    private static final String TALK_PREFERRED_ROOM_TAGS_DESCRIPTION = "Penalty per missing preferred tag in a talk's room, per minute";
    private static final String TALK_UNDESIRED_ROOM_TAGS_DESCRIPTION = "Penalty per undesired tag in a talk's room, per minute";

    private boolean strict;

    public ConferenceSchedulingXlsxFileIO() {
        this(true);
    }

    public ConferenceSchedulingXlsxFileIO(boolean strict) {
        super();
        this.strict = strict;
    }

    @Override
    public ConferenceSolution read(File inputSolutionFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputSolutionFile))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            return new ConferenceSchedulingXlsxReader(workbook).read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                    + inputSolutionFile + ").", e);
        }
    }

    @Override
    public void write(ConferenceSolution conferenceSolution, File file) {
        throw new UnsupportedOperationException("Unsupported solution write");
    }

    private class ConferenceSchedulingXlsxReader extends AbstractXlsxReader<ConferenceSolution> {

        private Map<String, TalkType> totalTalkTypeMap;
        private Set<String> totalTimeslotTagSet;
        private Set<String> totalRoomTagSet;
        private Map<String, Talk> totalTalkCodeMap;

        public ConferenceSchedulingXlsxReader(XSSFWorkbook workbook) {
            super(workbook, ConferenceScheduling.PATH_TO_SOLVER_CONFIG);
        }

        @Override
        public ConferenceSolution read() {
            solution = new ConferenceSolution();
            totalTalkTypeMap = new HashMap<>();
            totalTimeslotTagSet = new HashSet<>();
            totalRoomTagSet = new HashSet<>();
            totalTalkCodeMap = new HashMap<>();
            readConfiguration();
            readTimeslotList();
            readRoomList();
            readSpeakerList();
            readTalkList();
            // Needed for merging in the sheet Rooms views
            solution.getTimeslotList().sort(Comparator.comparing(Timeslot::getStartDateTime)
                    .thenComparing(Comparator.comparing(Timeslot::getEndDateTime).reversed()));
            return solution;
        }

        private void readConfiguration() {
            nextSheet("Configuration");
            nextRow();
            readHeaderCell("Conference name");
            solution.setConferenceName(nextStringCell().getStringCellValue());
            if (strict && !VALID_NAME_PATTERN.matcher(solution.getConferenceName()).matches()) {
                throw new IllegalStateException(currentPosition() + ": The conference name (" + solution.getConferenceName()
                        + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
            }
            ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration();

            readIntConstraintParameterLine("Minimum consecutive talks pause in minutes",
                    constraintConfiguration::setMinimumConsecutiveTalksPauseInMinutes,
                    "The amount of time a speaker needs between 2 talks");
            readScoreConstraintHeaders();
            constraintConfiguration.setId(0L);

            constraintConfiguration.setRoomUnavailableTimeslot(readScoreConstraintLine(ROOM_UNAVAILABLE_TIMESLOT,
                    ROOM_UNAVAILABLE_TIMESLOT_DESCRIPTION));
            constraintConfiguration.setRoomConflict(readScoreConstraintLine(ROOM_CONFLICT,
                    ROOM_CONFLICT_DESCRIPTION));
            constraintConfiguration.setSpeakerUnavailableTimeslot(readScoreConstraintLine(SPEAKER_UNAVAILABLE_TIMESLOT,
                    SPEAKER_UNAVAILABLE_TIMESLOT_DESCRIPTION));
            constraintConfiguration.setSpeakerConflict(readScoreConstraintLine(SPEAKER_CONFLICT,
                    SPEAKER_CONFLICT_DESCRIPTION));
            constraintConfiguration.setTalkPrerequisiteTalks(readScoreConstraintLine(TALK_PREREQUISITE_TALKS,
                    TALK_PREREQUISITE_TALKS_DESCRIPTION));
            constraintConfiguration.setTalkMutuallyExclusiveTalksTags(readScoreConstraintLine(TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS,
                    TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS_DESCRIPTION));
            constraintConfiguration.setConsecutiveTalksPause(readScoreConstraintLine(CONSECUTIVE_TALKS_PAUSE,
                    CONSECUTIVE_TALKS_PAUSE_DESCRIPTION));
            constraintConfiguration.setCrowdControl(readScoreConstraintLine(CROWD_CONTROL,
                    CROWD_CONTROL_DESCRIPTION));

            constraintConfiguration.setSpeakerRequiredTimeslotTags(readScoreConstraintLine(SPEAKER_REQUIRED_TIMESLOT_TAGS,
                    SPEAKER_REQUIRED_TIMESLOT_TAGS_DESCRIPTION));
            constraintConfiguration.setSpeakerProhibitedTimeslotTags(readScoreConstraintLine(SPEAKER_PROHIBITED_TIMESLOT_TAGS,
                    SPEAKER_PROHIBITED_TIMESLOT_TAGS_DESCRIPTION));
            constraintConfiguration.setTalkRequiredTimeslotTags(readScoreConstraintLine(TALK_REQUIRED_TIMESLOT_TAGS,
                    TALK_REQUIRED_TIMESLOT_TAGS_DESCRIPTION));
            constraintConfiguration.setTalkProhibitedTimeslotTags(readScoreConstraintLine(TALK_PROHIBITED_TIMESLOT_TAGS,
                    TALK_PROHIBITED_TIMESLOT_TAGS_DESCRIPTION));
            constraintConfiguration.setSpeakerRequiredRoomTags(readScoreConstraintLine(SPEAKER_REQUIRED_ROOM_TAGS,
                    SPEAKER_REQUIRED_ROOM_TAGS_DESCRIPTION));
            constraintConfiguration.setSpeakerProhibitedRoomTags(readScoreConstraintLine(SPEAKER_PROHIBITED_ROOM_TAGS,
                    SPEAKER_PROHIBITED_ROOM_TAGS_DESCRIPTION));
            constraintConfiguration.setTalkRequiredRoomTags(readScoreConstraintLine(TALK_REQUIRED_ROOM_TAGS,
                    TALK_REQUIRED_ROOM_TAGS_DESCRIPTION));
            constraintConfiguration.setTalkProhibitedRoomTags(readScoreConstraintLine(TALK_PROHIBITED_ROOM_TAGS,
                    TALK_PROHIBITED_ROOM_TAGS_DESCRIPTION));

            constraintConfiguration.setPublishedTimeslot(readScoreConstraintLine(PUBLISHED_TIMESLOT,
                    PUBLISHED_TIMESLOT_DESCRIPTION));

            constraintConfiguration.setPublishedRoom(readScoreConstraintLine(PUBLISHED_ROOM,
                    PUBLISHED_ROOM_DESCRIPTION));
            constraintConfiguration.setThemeTrackConflict(readScoreConstraintLine(THEME_TRACK_CONFLICT,
                    THEME_TRACK_CONFLICT_DESCRIPTION));
            constraintConfiguration.setThemeTrackRoomStability(readScoreConstraintLine(THEME_TRACK_ROOM_STABILITY,
                    THEME_TRACK_ROOM_STABILITY_DESCRIPTION));
            constraintConfiguration.setSectorConflict(readScoreConstraintLine(SECTOR_CONFLICT,
                    SECTOR_CONFLICT_DESCRIPTION));
            constraintConfiguration.setAudienceTypeDiversity(readScoreConstraintLine(AUDIENCE_TYPE_DIVERSITY,
                    AUDIENCE_TYPE_DIVERSITY_DESCRIPTION));
            constraintConfiguration.setAudienceTypeThemeTrackConflict(readScoreConstraintLine(AUDIENCE_TYPE_THEME_TRACK_CONFLICT,
                    AUDIENCE_TYPE_THEME_TRACK_CONFLICT_DESCRIPTION));
            constraintConfiguration.setAudienceLevelDiversity(readScoreConstraintLine(AUDIENCE_LEVEL_DIVERSITY,
                    AUDIENCE_LEVEL_DIVERSITY_DESCRIPTION));
            constraintConfiguration.setContentAudienceLevelFlowViolation(readScoreConstraintLine(CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION,
                    CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION_DESCRIPTION));
            constraintConfiguration.setContentConflict(readScoreConstraintLine(CONTENT_CONFLICT,
                    CONTENT_CONFLICT_DESCRIPTION));
            constraintConfiguration.setLanguageDiversity(readScoreConstraintLine(LANGUAGE_DIVERSITY,
                    LANGUAGE_DIVERSITY_DESCRIPTION));
            constraintConfiguration.setSameDayTalks(readScoreConstraintLine(SAME_DAY_TALKS,
                    SAME_DAY_TALKS_DESCRIPTION));
            constraintConfiguration.setPopularTalks(readScoreConstraintLine(POPULAR_TALKS,
                    POPULAR_TALKS_DESCRIPTION));

            constraintConfiguration.setSpeakerPreferredTimeslotTags(readScoreConstraintLine(SPEAKER_PREFERRED_TIMESLOT_TAGS,
                    SPEAKER_PREFERRED_TIMESLOT_TAGS_DESCRIPTION));
            constraintConfiguration.setSpeakerUndesiredTimeslotTags(readScoreConstraintLine(SPEAKER_UNDESIRED_TIMESLOT_TAGS,
                    SPEAKER_UNDESIRED_TIMESLOT_TAGS_DESCRIPTION));
            constraintConfiguration.setTalkPreferredTimeslotTags(readScoreConstraintLine(TALK_PREFERRED_TIMESLOT_TAGS,
                    TALK_PREFERRED_TIMESLOT_TAGS_DESCRIPTION));
            constraintConfiguration.setTalkUndesiredTimeslotTags(readScoreConstraintLine(TALK_UNDESIRED_TIMESLOT_TAGS,
                    TALK_UNDESIRED_TIMESLOT_TAGS_DESCRIPTION));
            constraintConfiguration.setSpeakerPreferredRoomTags(readScoreConstraintLine(SPEAKER_PREFERRED_ROOM_TAGS,
                    SPEAKER_PREFERRED_ROOM_TAGS_DESCRIPTION));
            constraintConfiguration.setSpeakerUndesiredRoomTags(readScoreConstraintLine(SPEAKER_UNDESIRED_ROOM_TAGS,
                    SPEAKER_UNDESIRED_ROOM_TAGS_DESCRIPTION));
            constraintConfiguration.setTalkPreferredRoomTags(readScoreConstraintLine(TALK_PREFERRED_ROOM_TAGS,
                    TALK_PREFERRED_ROOM_TAGS_DESCRIPTION));
            constraintConfiguration.setTalkUndesiredRoomTags(readScoreConstraintLine(TALK_UNDESIRED_ROOM_TAGS,
                    TALK_UNDESIRED_ROOM_TAGS_DESCRIPTION));

            solution.setConstraintConfiguration(constraintConfiguration);
        }

        private void readTimeslotList() {
            nextSheet("Timeslots");
            nextRow(false);
            readHeaderCell("Day");
            readHeaderCell("Start");
            readHeaderCell("End");
            readHeaderCell("Talk types");
            readHeaderCell("Tags");
            List<TalkType> talkTypeList = new ArrayList<>();
            List<Timeslot> timeslotList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            long talkTypeId = 0L;
            while (nextRow()) {
                Timeslot timeslot = new Timeslot();
                timeslot.setId(id++);
                LocalDate day = LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER);
                LocalTime startTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                if (startTime.compareTo(endTime) >= 0) {
                    throw new IllegalStateException(currentPosition() + ": The startTime (" + startTime
                            + ") must be less than the endTime (" + endTime + ").");
                }
                timeslot.setStartDateTime(LocalDateTime.of(day, startTime));
                timeslot.setEndDateTime(LocalDateTime.of(day, endTime));
                String[] talkTypeNames = nextStringCell().getStringCellValue().split(", ");
                Set<TalkType> talkTypeSet = new LinkedHashSet<>(talkTypeNames.length);
                for (String talkTypeName : talkTypeNames) {
                    TalkType talkType = totalTalkTypeMap.get(talkTypeName);
                    if (talkType == null) {
                        talkType = new TalkType(talkTypeId);
                        talkTypeId++;
                        if (strict && !VALID_TAG_PATTERN.matcher(talkTypeName).matches()) {
                            throw new IllegalStateException(currentPosition()
                                    + ": The timeslot (" + timeslot + ")'s talkType (" + talkTypeName
                                    + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                        }
                        talkType.setName(talkTypeName);
                        talkType.setCompatibleTimeslotSet(new LinkedHashSet<>());
                        talkType.setCompatibleRoomSet(new LinkedHashSet<>());
                        totalTalkTypeMap.put(talkTypeName, talkType);
                        talkTypeList.add(talkType);
                    }
                    talkTypeSet.add(talkType);
                    talkType.getCompatibleTimeslotSet().add(timeslot);
                }
                if (talkTypeSet.isEmpty()) {
                    throw new IllegalStateException(currentPosition()
                            + ": The timeslot (" + timeslot + ")'s talk types (" + timeslot.getTalkTypeSet()
                            + ") must not be empty.");
                }
                timeslot.setTalkTypeSet(talkTypeSet);
                timeslot.setTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : timeslot.getTagSet()) {
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition()
                                + ": The timeslot (" + timeslot + ")'s tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                totalTimeslotTagSet.addAll(timeslot.getTagSet());
                timeslotList.add(timeslot);
            }
            solution.setTimeslotList(timeslotList);
            solution.setTalkTypeList(talkTypeList);
        }

        private void readRoomList() {
            nextSheet("Rooms");
            nextRow(false);
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readTimeslotDaysHeaders();
            nextRow(false);
            readHeaderCell("Name");
            readHeaderCell("Capacity");
            readHeaderCell("Talk types");
            readHeaderCell("Tags");
            readTimeslotHoursHeaders();
            List<Room> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Room room = new Room();
                room.setId(id++);
                room.setName(nextStringCell().getStringCellValue());
                if (strict && !VALID_NAME_PATTERN.matcher(room.getName()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The room name (" + room.getName()
                            + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
                room.setCapacity(getNextStrictlyPositiveIntegerCell("room name (" + room.getName(), "capacity"));
                String[] talkTypeNames = nextStringCell().getStringCellValue().split(", ");
                Set<TalkType> talkTypeSet;
                if (talkTypeNames.length == 0 || (talkTypeNames.length == 1 && talkTypeNames[0].isEmpty())) {
                    talkTypeSet = new LinkedHashSet<>(totalTalkTypeMap.values());
                    for (TalkType talkType : talkTypeSet) {
                        talkType.getCompatibleRoomSet().add(room);
                    }
                } else {
                    talkTypeSet = new LinkedHashSet<>(talkTypeNames.length);
                    for (String talkTypeName : talkTypeNames) {
                        TalkType talkType = totalTalkTypeMap.get(talkTypeName);
                        if (talkType == null) {
                            throw new IllegalStateException(currentPosition()
                                    + ": The room (" + room + ")'s talkType (" + talkTypeName
                                    + ") does not exist in the talk types (" + totalTalkTypeMap.keySet()
                                    + ") of the other sheet (Timeslots).");
                        }
                        talkTypeSet.add(talkType);
                        talkType.getCompatibleRoomSet().add(room);
                    }
                }
                room.setTalkTypeSet(talkTypeSet);
                room.setTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : room.getTagSet()) {
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The room (" + room + ")'s tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                totalRoomTagSet.addAll(room.getTagSet());
                Set<Timeslot> unavailableTimeslotSet = new LinkedHashSet<>();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    XSSFCell cell = nextStringCell();
                    if (Objects.equals(extractColor(cell, UNAVAILABLE_COLOR), UNAVAILABLE_COLOR)) {
                        unavailableTimeslotSet.add(timeslot);
                    }
                    if (!cell.getStringCellValue().isEmpty()) {
                        throw new IllegalStateException(currentPosition() + ": The cell (" + cell.getStringCellValue()
                                + ") should be empty. Use the talks sheet pre-assign rooms and timeslots.");
                    }
                }
                room.setUnavailableTimeslotSet(unavailableTimeslotSet);
                roomList.add(room);
            }
            solution.setRoomList(roomList);
        }

        private void readSpeakerList() {
            nextSheet("Speakers");
            nextRow(false);
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readTimeslotDaysHeaders();
            nextRow(false);
            readHeaderCell("Name");
            readHeaderCell("Required timeslot tags");
            readHeaderCell("Preferred timeslot tags");
            readHeaderCell("Prohibited timeslot tags");
            readHeaderCell("Undesired timeslot tags");
            readHeaderCell("Required room tags");
            readHeaderCell("Preferred room tags");
            readHeaderCell("Prohibited room tags");
            readHeaderCell("Undesired room tags");

            readTimeslotHoursHeaders();
            List<Speaker> speakerList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Speaker speaker = new Speaker();
                speaker.setId(id++);
                speaker.setName(nextStringCell().getStringCellValue());
                if (strict && !VALID_NAME_PATTERN.matcher(speaker.getName()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The speaker name (" + speaker.getName()
                            + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
                speaker.setRequiredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getRequiredTimeslotTagSet());
                speaker.setPreferredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getPreferredTimeslotTagSet());
                speaker.setProhibitedTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getProhibitedTimeslotTagSet());
                speaker.setUndesiredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getUndesiredTimeslotTagSet());
                speaker.setRequiredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getRequiredRoomTagSet());
                speaker.setPreferredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getPreferredRoomTagSet());
                speaker.setProhibitedRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getProhibitedRoomTagSet());
                speaker.setUndesiredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getUndesiredRoomTagSet());
                Set<Timeslot> unavailableTimeslotSet = new LinkedHashSet<>();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    XSSFCell cell = nextStringCell();
                    if (Objects.equals(extractColor(cell, UNAVAILABLE_COLOR), UNAVAILABLE_COLOR)) {
                        unavailableTimeslotSet.add(timeslot);
                    }
                    if (!cell.getStringCellValue().isEmpty()) {
                        throw new IllegalStateException(currentPosition() + ": The cell (" + cell.getStringCellValue()
                                + ") should be empty. Use the other sheet (Talks) to pre-assign rooms and timeslots.");
                    }
                }
                speaker.setUnavailableTimeslotSet(unavailableTimeslotSet);
                speakerList.add(speaker);
            }
            solution.setSpeakerList(speakerList);
        }

        private void readTalkList() {
            Map<String, Speaker> speakerMap = solution.getSpeakerList().stream().collect(
                    toMap(Speaker::getName, speaker -> speaker));
            nextSheet("Talks");
            nextRow(false);
            readHeaderCell("Code");
            readHeaderCell("Title");
            readHeaderCell("Talk type");
            readHeaderCell("Speakers");
            readHeaderCell("Theme track tags");
            readHeaderCell("Sector tags");
            readHeaderCell("Audience types");
            readHeaderCell("Audience level");
            readHeaderCell("Content tags");
            readHeaderCell("Language");
            readHeaderCell("Required timeslot tags");
            readHeaderCell("Preferred timeslot tags");
            readHeaderCell("Prohibited timeslot tags");
            readHeaderCell("Undesired timeslot tags");
            readHeaderCell("Required room tags");
            readHeaderCell("Preferred room tags");
            readHeaderCell("Prohibited room tags");
            readHeaderCell("Undesired room tags");
            readHeaderCell("Mutually exclusive talks tags");
            readHeaderCell("Prerequisite talks codes");
            readHeaderCell("Favorite count");
            readHeaderCell("Crowd control risk");
            readHeaderCell("Pinned by user");
            readHeaderCell("Timeslot day");
            readHeaderCell("Start");
            readHeaderCell("End");
            readHeaderCell("Room");
            readHeaderCell("Published Timeslot");
            readHeaderCell("Published Start");
            readHeaderCell("Published End");
            readHeaderCell("Published Room");
            List<Talk> talkList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            Map<Pair<LocalDateTime, LocalDateTime>, Timeslot> timeslotMap = solution.getTimeslotList().stream().collect(
                    Collectors.toMap(timeslot -> Pair.of(timeslot.getStartDateTime(), timeslot.getEndDateTime()),
                            Function.identity()));
            Map<String, Room> roomMap = solution.getRoomList().stream().collect(
                    Collectors.toMap(Room::getName, Function.identity()));
            Map<Talk, Set<String>> talkToPrerequisiteTalkSetMap = new HashMap<>();
            while (nextRow()) {
                Talk talk = new Talk();
                talk.setId(id++);
                talk.setCode(nextStringCell().getStringCellValue());
                totalTalkCodeMap.put(talk.getCode(), talk);
                if (strict && !VALID_CODE_PATTERN.matcher(talk.getCode()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The talk code (" + talk.getCode()
                            + ") must match to the regular expression (" + VALID_CODE_PATTERN + ").");
                }
                talk.setTitle(nextStringCell().getStringCellValue());
                String talkTypeName = nextStringCell().getStringCellValue();
                TalkType talkType = totalTalkTypeMap.get(talkTypeName);
                if (talkType == null) {
                    throw new IllegalStateException(currentPosition()
                            + ": The talk (" + talk + ")'s talkType (" + talkTypeName
                            + ") does not exist in the talk types (" + totalTalkTypeMap.keySet()
                            + ") of the other sheet (Timeslots).");
                }
                talk.setTalkType(talkType);
                talk.setSpeakerList(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).map(speakerName -> {
                            Speaker speaker = speakerMap.get(speakerName);
                            if (speaker == null) {
                                throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                                        + ") has a speaker (" + speakerName + ") that doesn't exist in the speaker list.");
                            }
                            return speaker;
                        }).collect(toList()));
                talk.setThemeTrackTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getThemeTrackTagSet()) {
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s theme tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setSectorTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getSectorTagSet()) {
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s sector tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setAudienceTypeSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String audienceType : talk.getAudienceTypeSet()) {
                    if (strict && !VALID_TAG_PATTERN.matcher(audienceType).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s audience type (" + audienceType
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setAudienceLevel(getNextStrictlyPositiveIntegerCell("talk with code (" + talk.getCode(), "an audience level"));
                talk.setContentTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getContentTagSet()) {
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s content tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setLanguage(nextStringCell().getStringCellValue());
                talk.setRequiredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getRequiredTimeslotTagSet());
                talk.setPreferredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getPreferredTimeslotTagSet());
                talk.setProhibitedTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getProhibitedTimeslotTagSet());
                talk.setUndesiredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getUndesiredTimeslotTagSet());
                talk.setRequiredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getRequiredRoomTagSet());
                talk.setPreferredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getPreferredRoomTagSet());
                talk.setProhibitedRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getProhibitedRoomTagSet());
                talk.setUndesiredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getUndesiredRoomTagSet());
                talk.setMutuallyExclusiveTalksTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(Collectors.toCollection(LinkedHashSet::new)));
                talkToPrerequisiteTalkSetMap.put(talk, Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(Collectors.toCollection(LinkedHashSet::new)));
                talk.setFavoriteCount(getNextPositiveIntegerCell("talk with code (" + talk.getCode(), "a Favorite count"));
                talk.setCrowdControlRisk(getNextPositiveIntegerCell("talk with code (" + talk.getCode(), "a crowd control risk"));
                talk.setPinnedByUser(nextBooleanCell().getBooleanCellValue());
                talk.setTimeslot(extractTimeslot(timeslotMap, talk));
                talk.setRoom(extractRoom(roomMap, talk));
                talk.setPublishedTimeslot(extractTimeslot(timeslotMap, talk));
                talk.setPublishedRoom(extractRoom(roomMap, talk));

                talkList.add(talk);
            }

            setPrerequisiteTalkSets(talkToPrerequisiteTalkSetMap);
            solution.setTalkList(talkList);
        }

        private Timeslot extractTimeslot(Map<Pair<LocalDateTime, LocalDateTime>, Timeslot> timeslotMap, Talk talk) {
            Timeslot assignedTimeslot;
            String dateString = nextStringCell().getStringCellValue();
            String startTimeString = nextStringCell().getStringCellValue();
            String endTimeString = nextStringCell().getStringCellValue();
            if (!dateString.isEmpty() || !startTimeString.isEmpty() || !endTimeString.isEmpty()) {
                LocalDateTime startDateTime;
                LocalDateTime endDateTime;
                try {
                    startDateTime = LocalDateTime.of(LocalDate.parse(dateString, DAY_FORMATTER),
                            LocalTime.parse(startTimeString, TIME_FORMATTER));
                    endDateTime = LocalDateTime.of(LocalDate.parse(dateString, DAY_FORMATTER),
                            LocalTime.parse(endTimeString, TIME_FORMATTER));
                } catch (DateTimeParseException e) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ") has a timeslot date (" + dateString
                            + "), startTime (" + startTimeString + ") and endTime (" + endTimeString
                            + ") that doesn't parse as a date or time.", e);
                }

                assignedTimeslot = timeslotMap.get(Pair.of(startDateTime, endDateTime));
                if (assignedTimeslot == null) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ") has a timeslot date (" + dateString
                            + "), startTime (" + startTimeString + ") and endTime (" + endTimeString
                            + ") that doesn't exist in the other sheet (Timeslots).");
                }

                return assignedTimeslot;
            }

            return null;
        }

        private Room extractRoom(Map<String, Room> roomMap, Talk talk) {
            String roomName = nextStringCell().getStringCellValue();
            if (!roomName.isEmpty()) {
                Room room = roomMap.get(roomName);
                if (room == null) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ") has a roomName (" + roomName
                            + ") that doesn't exist in the other sheet (Rooms).");
                }
                return room;
            }

            return null;
        }

        private int getNextStrictlyPositiveIntegerCell(String classSpecifier, String columnName) {
            double cellValueDouble = nextNumericCell().getNumericCellValue();
            if (strict && (cellValueDouble <= 0 || cellValueDouble != Math.floor(cellValueDouble))) {
                throw new IllegalStateException(currentPosition() + ": The" + classSpecifier
                        + ")'s has " + columnName + " (" + cellValueDouble + ") that isn't a strictly positive integer number.");
            }
            return (int) cellValueDouble;
        }

        private int getNextPositiveIntegerCell(String classSpecifier, String columnName) {
            double cellValueDouble = nextNumericCell().getNumericCellValue();
            if (strict && (cellValueDouble < 0 || cellValueDouble != Math.floor(cellValueDouble))) {
                throw new IllegalStateException(currentPosition() + ": The " + classSpecifier
                        + ")'s has " + columnName + " (" + cellValueDouble + ") that isn't a positive integer number.");
            }
            return (int) cellValueDouble;
        }

        private void verifyTimeslotTags(Set<String> timeslotTagSet) {
            for (String tag : timeslotTagSet) {
                if (!totalTimeslotTagSet.contains(tag)) {
                    throw new IllegalStateException(currentPosition() + ": The timeslot tag (" + tag
                            + ") does not exist in the tags (" + totalTimeslotTagSet
                            + ") of the other sheet (Timeslots).");
                }
            }
        }

        private void verifyRoomTags(Set<String> roomTagSet) {
            for (String tag : roomTagSet) {
                if (!totalRoomTagSet.contains(tag)) {
                    throw new IllegalStateException(currentPosition() + ": The room tag (" + tag
                            + ") does not exist in the tags (" + totalRoomTagSet + ") of the other sheet (Rooms).");
                }
            }
        }

        private void setPrerequisiteTalkSets(Map<Talk, Set<String>> talkToPrerequisiteTalkSetMap) {
            for (Map.Entry<Talk, Set<String>> entry : talkToPrerequisiteTalkSetMap.entrySet()) {
                Talk currentTalk = entry.getKey();
                Set<Talk> prerequisiteTalkSet = new HashSet<>();
                for (String prerequisiteTalkCode : entry.getValue()) {
                    Talk prerequisiteTalk = totalTalkCodeMap.get(prerequisiteTalkCode);
                    if (prerequisiteTalk == null) {
                        throw new IllegalStateException("The talk (" + currentTalk.getCode()
                                + ") has a prerequisite talk (" + prerequisiteTalkCode + ") that doesn't exist in the talk list.");
                    }
                    prerequisiteTalkSet.add(prerequisiteTalk);
                }
                currentTalk.setPrerequisiteTalkSet(prerequisiteTalkSet);
            }
        }

        private void readTimeslotDaysHeaders() {
            LocalDate previousTimeslotDay = null;
            for (Timeslot timeslot : solution.getTimeslotList()) {
                LocalDate timeslotDay = timeslot.getDate();
                if (timeslotDay.equals(previousTimeslotDay)) {
                    readHeaderCell("");
                } else {
                    readHeaderCell(DAY_FORMATTER.format(timeslotDay));
                    previousTimeslotDay = timeslotDay;
                }
            }
        }

        private void readTimeslotHoursHeaders() {
            for (Timeslot timeslot : solution.getTimeslotList()) {
                readHeaderCell(TIME_FORMATTER.format(timeslot.getStartDateTime())
                        + "-" + TIME_FORMATTER.format(timeslot.getEndDateTime()));
            }
        }
    }
}
