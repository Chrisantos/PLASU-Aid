package com.eze.chrisantus.emergencyreporter.Utils;

public class ListPOJO {
    private String info, aux;

    public ListPOJO() {
    }

    public ListPOJO(String info, String aux) {
        this.info = info;
        this.aux = aux;
    }

    public String getInFo() {
        return info;
    }

    public void setInFo(String info) {
        this.info = info;
    }

    public String getAux() {
        return aux;
    }

    public void setAux(String aux) {
        this.aux = aux;
    }
}
