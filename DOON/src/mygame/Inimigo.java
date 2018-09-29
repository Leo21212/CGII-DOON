/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author LUCAS
 */
public class Inimigo {
    public int vida;
    
    boolean tiro_delay,dano_delay;
    float tempo_disparo,tempo_dano;
    private Geometry inimigo;

    public boolean isTiro_delay() {
        return tiro_delay;
    }

    public void setTiro_delay(boolean tiro_delay) {
        this.tiro_delay = tiro_delay;
    }

    public Geometry getInimigo() {
        return inimigo;
    }

    public void setInimigo(Geometry inimigo) {
        this.inimigo = inimigo;
    }
    public Inimigo(Geometry inimigo)
    {        
        vida=3;             
        tiro_delay=false;
        dano_delay=false;
        tempo_dano=0;
        tempo_disparo=0;
        this.inimigo=inimigo;
    }
    
    public void Dano()
    {
        if(!dano_delay)
        {
        vida--;
        dano_delay=false;
        }
    }
     private void DelayDano(float tpf)
    {
        tempo_dano+=tpf;
        if(tempo_dano>=0.5)
        {
            dano_delay=false;
            tempo_dano=0;
        }
    }
    
    private void DelayTiro(float tpf)
    {
        tempo_disparo+=tpf;
        if(tempo_disparo>=3)
        {
            tiro_delay=false;
            tempo_disparo=0;
        }
    }
   
    public void Acoes(float tpf)
    {
       
        if(tiro_delay)
            DelayTiro(tpf);
        
        if(dano_delay)
            DelayDano(tpf);
    }
    public void Atirar()
    {
        tiro_delay=true;
    }
    public boolean Atirar_Valido()
    {
        return !tiro_delay;
    }
    public boolean Morte()
    {
        if(vida-1<=0)
            return true;
        else
            return false;
    }
    

   
}
