package org.agoncal.application.petstore.service;

public interface BloatedDBPopulatorMXBean {

    public static final String OBJECT_NAME = "com.jclarity:type=Problems,name=problem";

    public void bloatDB(int numberOfEntries);

    public void emptyDB();
}
