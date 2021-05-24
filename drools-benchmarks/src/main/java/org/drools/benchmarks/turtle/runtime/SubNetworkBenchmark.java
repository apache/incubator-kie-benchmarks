/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.turtle.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

public class SubNetworkBenchmark extends AbstractSimpleFusionRuntimeBenchmark {

    @Param({"true", "false"})
    private boolean nestedAccumulates;

    @Param({"2"})
    private int joinCount;

    @Param({"3"})
    private int segmentCount;

    @Param({"5"})
    private int objectsPerSegment;

    @Param({"true", "false"})
    private boolean perSegmentUpdate;

    private TaskConfiguration config;

    @Override
    public void addResources() {
        String body = nestedAccumulates ? nestedAccumulates(segmentCount) : sequentialAccumulates(segmentCount);
        String drl = getRules(segmentCount, body);
        addDrlResource(drl);
    }

    @Setup(Level.Iteration)
    public void setup() {
        List<List<Node>> nodes = new ArrayList<>();

        for(int i=0; i < segmentCount; i++) {
            nodes.add(createNodes(i, objectsPerSegment));
        }

        List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        List<Map<Long, FactHandle>> handles = new ArrayList<>();

        for (List<Node> nodeList : nodes) {
            Map<Long, FactHandle> handlesMap = new HashMap<>(nodeList.size());
            for (int j = 0; j < nodeList.size(); j++) {
                InternalFactHandle handle = (InternalFactHandle) ksession.insert(nodeList.get(j));
                handlesMap.put(handle.getId(), handle);
            }
            handles.add(handlesMap);
        }

        List<List<Long>>  ids = new ArrayList<>();
        for (int i = 0; i < handles.size(); i++) {
            List<Long> idList = new ArrayList<>(handles.get(i).keySet());
            Collections.shuffle(idList, new Random(0));
            ids.add(idList);
        }

        int[] segmentsToVisit = new int[] {0, 1, 2};
        config = TaskConfiguration.configure(segmentsToVisit, list, handles, ids, perSegmentUpdate);
    }

    @Benchmark
    public KieSession timeFactsInsertionAndRulesFiring() {
        ksession.fireAllRules();
        iterate();
        return ksession;
    }

    private void iterate() {
        int[] segmentsToVisit = config.segmentsToVisit;
        List<Integer> list = config.list;
        List<Map<Long, FactHandle>> handles = config.handles;
        List<List<Long>> ids = config.ids;
        boolean perSegmentUpdate = config.perSegmentUpdate;

        for (int i = 0; i < segmentsToVisit.length; i++) {
            iterate(new int[] {segmentsToVisit[i]},
                    list, ksession,
                    handles, ids, new RemoveAddTask(), perSegmentUpdate);
        }

        for (int i = 0; i < segmentsToVisit.length; i++) {
            iterate(new int[] {segmentsToVisit[i]},
                    list, ksession,
                    handles, ids, new UpdateTask(), perSegmentUpdate);
        }

        iterate(segmentsToVisit,
                list, ksession,
                handles, ids, new UpdateTask(), perSegmentUpdate);

        iterate(segmentsToVisit,
                list, ksession,
                handles, ids, new RemoveAddTask(), perSegmentUpdate);
    }

    private void iterate(int[] segmentsToVisit, List<Integer> list, KieSession ksession,
                         List<Map<Long, FactHandle>> handles, List<List<Long>> ids, Task task, boolean perSegmentUpdate) {
        int[] intPosArray = new int[segmentsToVisit.length];
        list.clear();
        for (int j = 0; j < segmentsToVisit.length; j++) {
            int segment = segmentsToVisit[j];
            List<Long> idList = ids.get(segment);
            Map<Long, FactHandle> handlesMap = handles.get(segment);
            int intPos = intPosArray[j];
            int batchSize = idList.size();
            for (int k = 0; k < batchSize; k++) {
                task.execute(ksession, handlesMap, idList, intPos + k);
            }
            intPosArray[j] = intPos + batchSize;
            if (perSegmentUpdate) {
                ksession.fireAllRules();
            }
        }
        if (!perSegmentUpdate) {
            ksession.fireAllRules();
        }
    }

    private List<Node> createNodes(int segment, int nestedCount) {
        List<Node> nodes = new ArrayList<>();
        for (int j = 0; j < nestedCount; j++) {
            Node n = new Node(segment, getJoin(j), j );
            nodes.add(n);
        }
        return nodes;
    }

    public int getJoin(int value) {
        return value & (joinCount - 1);
    }

    private String getRules(int accCount, String acc) {
        String str =
                "import " + Node.class.getCanonicalName() + ";" +
                "import java.util.List;" +
                "global List list;\n" +
                "rule X when\n" +
                generateNodes(0) +
                acc +
                "\nthen\n" +
                "  list.add($n0" + (joinCount-1) + " + \":\" + $count" + accCount + ");\n" +
                "end";

        return str;
    }

    private String generateNodes(int segment) {
        String str = (joinCount == 1) ? "" : "(";
        for ( int i = 0; i < joinCount; i++) {
            if (i > 0) {
                str += " and ";
            }
            str += "$n" + segment + "" + i + " : Node(segment == " + segment + ", join == " + i + ")";
        }
        str +=  (joinCount == 1) ? "" : ")";

        return str;
    }

    private String nestedAccumulates(int segments) {
        String innerAcc = "\n" + indent((segments-1)*4) +
                "acc(" + generateNodes(1) + ",\n" +
                indent(4 + (segments-1)*4) + "$count1 : count($n1" + (joinCount-1) + ") )";

        String outerAcc = innerAcc;
        for (int i = 1; i < segments; i++) {
            outerAcc =
                    "\n" + indent((segments-i-1)*4) + "acc("
                            + generateNodes(i + 1) + " and "
                            + outerAcc + ",\n" +
                            indent(4 + (segments-i-1)*4) + "$count" + (i+1) + " : count($n" + (i+1) + "" + (joinCount-1) + ") )";
        }
        return outerAcc;
    }

    private static String indent(int size) {
        String str = "";
        for (int i = 0; i < size; i++) {
            str += " ";
        }

        return str;
    }

    private String sequentialAccumulates(int accCount) {
        String outerAcc = "";
        for (int i = 0; i < accCount; i++) {
            outerAcc = outerAcc +
                    "\nacc(" + generateNodes(i + 1) + ",\n" +
                    "     $count" + (i+1) + " : count($n" + (i+1) + "" + (joinCount-1) + "))";
        }
        return outerAcc;
    }

    public static class Node {
        private int value;
        private int join;
        private int segment;

        public Node(int segment, int join, int value) {
            this.value = value;
            this.join = join;
            this.segment = segment;
        }

        public int value() {
            return value;
        }

        public int join() {
            return join;
        }

        public int segment() {
            return segment;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Node node = (Node) o;

            if (value != node.value) {
                return false;
            }
            if (join != node.join) {
                return false;
            }
            return segment == node.segment;
        }

        @Override public int hashCode() {
            int result = value;
            result = 31 * result + join;
            result = 31 * result + segment;
            return result;
        }

        @Override public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", join=" + join +
                    ", segment=" + segment +
                    '}';
        }
    }

    public interface Task {
        void execute(KieSession ksession, Map<Long, FactHandle> handles, List<Long> ids, float pos);
    }

    public static class RemoveAddTask implements Task {
        public void execute(KieSession ksession, Map<Long, FactHandle> handles, List<Long> ids, float pos) {
            InternalFactHandle handle = (InternalFactHandle) handles.get(ids.get((int) pos)); // cast rounds it
            Object o = handle.getObject();

            handles.remove(handle.getId());
            ksession.delete(handle);

            handle = (InternalFactHandle) ksession.insert(o);

            ids.set((int) pos, handle.getId()); // cast rounds it
            handles.put(handle.getId(), handle);
        }
    }

    public static class UpdateTask implements Task {
        public void execute(KieSession ksession, Map<Long, FactHandle> handles, List<Long> ids, float pos) {
            InternalFactHandle handle = (InternalFactHandle) handles.get(ids.get((int) pos)); // cast rounds it
            Object o = handle.getObject();

            ksession.update(handle, o);
        }
    }

    public static class TaskConfiguration {
        /** Number of segments to generate, this is effectively the number of accumulates */
        private int[] segmentsToVisit;

        /** The handles maps per segment */
        private List<Map<Long, FactHandle>> handles;

        /** Call fire per segment task, or per part iteration. */
        private boolean perSegmentUpdate;

        /** The ids per segment. Note this is not the facthandle ID.
         * But uses the position of this list, to map to the FH ID, for look up in the map. */
        private List<List<Long>> ids;

        private List<Integer> list;

        private TaskConfiguration() {

        }

        public static TaskConfiguration configure(int[] segmentsToVisit, List<Integer> list,
                                                  List<Map<Long, FactHandle>> handles,
                                                  List<List<Long>> ids, boolean perSegmentUpdate) {
            TaskConfiguration t = new TaskConfiguration();
            t.segmentsToVisit = segmentsToVisit;
            t.list = list;
            t.handles = handles;
            t.ids = ids;
            t.perSegmentUpdate = perSegmentUpdate;

            return t;
        }

        public TaskConfiguration setSegmentsToVisit(int[] segmentsToVisit) {
            this.segmentsToVisit = segmentsToVisit;
            return this;
        }

        public TaskConfiguration setList(List<Integer> list) {
            this.list = list;
            return this;
        }

        public TaskConfiguration setHandles(List<Map<Long, FactHandle>> handles) {
            this.handles = handles;
            return this;
        }

        public TaskConfiguration setIds(List<List<Long>> ids) {
            this.ids = ids;
            return this;
        }

        public TaskConfiguration setPerSegmentUpdate(boolean perSegmentUpdate) {
            this.perSegmentUpdate = perSegmentUpdate;
            return this;
        }
    }

}
