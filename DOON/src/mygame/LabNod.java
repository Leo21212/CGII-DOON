/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author 152728
 */
public class LabNod {
    private int i,j,cont;
    private LabNod pai;
    public LabNod(int i, int j, int cont) {
        this.i = i;
        this.j = j;
        this.cont = cont;
    }
    public LabNod(int i, int j, int cont, LabNod pai) {
        this.i = i;
        this.j = j;
        this.cont = cont;
        this.pai=pai;
    }
    public void Pai(LabNod pai)
    {
        this.pai = pai;
       
    }

    public LabNod getPai() {
        return pai;
    }

    public void setPai(LabNod pai) {
        this.pai = pai;
    }
    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

   
    public int getCont() {
        return cont;
    }

    public void setCont(int cont) {
        this.cont = cont;
    }
}
