package com.officework.models;

import java.util.ArrayList;

public class DiagnosticPojo {


    public ArrayList<DiagnosticListPojo> getDiagnosticTestsList() {
        return DiagnosticTestsList;
    }

    public void setDiagnosticTestsList(ArrayList<DiagnosticListPojo> diagnosticTestsList) {
        DiagnosticTestsList = diagnosticTestsList;
    }

    private ArrayList<DiagnosticListPojo> DiagnosticTestsList;
}

