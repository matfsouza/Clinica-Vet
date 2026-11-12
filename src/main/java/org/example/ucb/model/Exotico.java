package org.example.ucb.model;

public class Exotico extends Animal {

    private String rfidex;
    private String notaFiscal;

    public Exotico(){
        super();
    }

    public Exotico(int id, String nome, String porte, int idade, String especie, String rfidex, String notaFiscal, Dono dono) {
        super(id, nome, porte, idade, especie, dono);
        this.rfidex = rfidex;
        this.notaFiscal = notaFiscal;
    }

    public String getRfidex() {
        return rfidex;
    }
    public void setRfidex(String rfidex) {
        this.rfidex = rfidex;
    }
    public String getNotaFiscal() {
        return notaFiscal;
    }
    public void setNotaFiscal(String notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

}
