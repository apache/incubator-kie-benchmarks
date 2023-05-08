package org.drools.benchmarks.reliability.proto.complex;

public class StringListContainerUtils {

    private StringListContainerUtils() {
    }

    public static void populateStringLists(StringListContainer container) {
        for (int i = 0; i < 10; i++) {
            addString(container, "ABCD" + i);
        }

    }
    public static void addString(StringListContainer container, String string) {
        container.getList1().add(string);
        container.getList2().add(string);
        container.getList3().add(string);
        container.getList4().add(string);
        container.getList5().add(string);
        container.getList6().add(string);
        container.getList7().add(string);
        container.getList8().add(string);
        container.getList9().add(string);
        container.getList10().add(string);
    }
}
