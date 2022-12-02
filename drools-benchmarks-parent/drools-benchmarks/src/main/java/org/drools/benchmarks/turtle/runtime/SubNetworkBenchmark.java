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
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

public class SubNetworkBenchmark extends AbstractSimpleRuntimeBenchmark {

    @Param({"true", "false"})
    private boolean nestedAccumulates;

    @Param({"2"})
    private int joinCount;

    @Param({"3"})
    private int segmentCount;

    @Param({"5", "15"})
    private int objectsPerSegment;

    @Param({"true", "false"})
    private boolean perSegmentUpdate;

    private List<List<Node>> nodes = new ArrayList<>();

    @Override
    public void addResources() {
        String body = nestedAccumulates ? nestedAccumulates(segmentCount) : sequentialAccumulates(segmentCount);
        String drl = getRules(segmentCount, body);
        System.out.println(drl);
        addDrlResource(drl);
    }

    @Setup(Level.Iteration)
    public void setup() {
        nodes.clear();
        for (int i=0; i < segmentCount; i++) {
            nodes.add(createNodes(i, objectsPerSegment));
        }
    }

    @Benchmark
    public int benchmark() {
        ksession = kieBase.newKieSession();

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

        ksession.fireAllRules();

        int[] segmentsToVisit = new int[] {0, 1, 2};
        iterate(segmentsToVisit, list, handles, ids);
        return list.size();
    }

    private void iterate(int[] segmentsToVisit, List<Integer> list, List<Map<Long, FactHandle>> handles, List<List<Long>> ids) {
        for (int i = 0; i < segmentsToVisit.length; i++) {
            iterate(new int[] {segmentsToVisit[i]}, list, handles, ids, new RemoveAddTask());
        }

        for (int i = 0; i < segmentsToVisit.length; i++) {
            iterate(new int[] {segmentsToVisit[i]}, list, handles, ids, new UpdateTask());
        }

        iterate(segmentsToVisit, list, handles, ids, new UpdateTask());

        iterate(segmentsToVisit, list, handles, ids, new RemoveAddTask());
    }

    private void iterate(int[] segmentsToVisit, List<Integer> list, List<Map<Long, FactHandle>> handles, List<List<Long>> ids, Task task) {
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
                "import " + Node.class.getCanonicalName() + ";\n" +
                "import java.util.List;\n" +
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
        void execute(KieSession ksession, Map<Long, FactHandle> handles, List<Long> ids, int pos);
    }

    public static class RemoveAddTask implements Task {
        public void execute(KieSession ksession, Map<Long, FactHandle> handles, List<Long> ids, int pos) {
            InternalFactHandle handle = (InternalFactHandle) handles.get(ids.get(pos));
            Object o = handle.getObject();

            handles.remove(handle.getId());
            ksession.delete(handle);

            handle = (InternalFactHandle) ksession.insert(o);

            ids.set(pos, handle.getId());
            handles.put(handle.getId(), handle);
        }
    }

    public static class UpdateTask implements Task {
        public void execute(KieSession ksession, Map<Long, FactHandle> handles, List<Long> ids, int pos) {
            InternalFactHandle handle = (InternalFactHandle) handles.get(ids.get(pos));
            Object o = handle.getObject();

            ksession.update(handle, o);
        }
    }
}
