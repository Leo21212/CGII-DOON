/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;


public class Player {
    public int vida;
    public int paint;
    public int balas;
    public int vida_maxima;
    public int balas_maxima;
    public int pontos;
    public int chaves;
    boolean tiro_delay,tiro_recarga,chave_delay,dano_delay;
    float tempo_disparo,tempo_recarga,tempo_chave,tempo_dano;
    
    public Player()
    {
        chaves=0;
        vida=100;
        paint=4;
        balas=10;
        vida_maxima=100;
        balas_maxima=25;
        pontos=0;
        tiro_delay=false;
        tiro_recarga=false;
        chave_delay=false;
        dano_delay=false;
        tempo_disparo=0;
        tempo_recarga=0;
        tempo_chave=0;
        tempo_dano=0;
    }
    private void Recarrega()
    {
        int recarga = 4-paint;
        if(balas-recarga>=0)
        {
            paint+=recarga;
            balas-=recarga;
        }
        else if(balas>0)
        {
            paint+=balas;
            balas=0;
        }           
    }
    public void AddVida(int vida)
    {
        if(this.vida+vida<=this.vida_maxima)
        {
            this.vida+=vida;
        }
        else
        {
            this.vida=this.vida_maxima;
        }
    }
    public boolean ChavePega()
    {
        if(chaves>=3)
        {
            return true;
        }
        else
        {
            return false;
        }
    }    
    
    public void AddChave()
    {
        if(!chave_delay)
        {
        chaves+=1;
        chave_delay=true;
        }
    }
    public void Dano(int dan)
    {
        if(!dano_delay)
        {
        vida-=dan;
        dano_delay=true;
        }
    }
     private void DelayDano(float tpf)
    {
        tempo_dano+=tpf;
        if(tempo_dano>=1)
        {
            dano_delay=false;
            tempo_dano=0;
        }
    }
    public void AddAmmo(int ammo)
    {
        if(this.balas+ammo<=this.balas_maxima)
        {
            this.balas+=ammo;
        }
        else
        {
            this.balas=this.balas_maxima;
        }
    }
    private void DelayTiro(float tpf)
    {
        tempo_disparo+=tpf;
        if(tempo_disparo>=1)
        {
            tiro_delay=false;
            tempo_disparo=0;
        }
    }
    private void DelayRecarregar(float tpf)
    {
        tempo_recarga+=tpf;
        if(tempo_recarga>=3.5)
        {
            tiro_recarga=false;
            tempo_recarga=0;
        }
    }
    private void DelayChave(float tpf)
    {
        tempo_chave+=tpf;
        if(tempo_chave>=3.0)
        {
            chave_delay=false;
            tempo_chave=0;
        }
    }
    
    public void Acoes(float tpf)
    {
        if(tiro_recarga)
            DelayRecarregar(tpf);
        if(tiro_delay)
            DelayTiro(tpf);
        if(chave_delay)
            DelayChave(tpf);
        if(dano_delay)
            DelayDano(tpf);
    }
    public boolean TemBalaPaint()
    {
        if(paint>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean TemBala()
    {
        if(balas>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void Atirar()
    {
        tiro_delay=true;
        paint--;
    }
    public void Recarregar()
    {
       if(paint<4 && !tiro_recarga)
       {
       Recarrega();
       tiro_recarga=true;
       }
    }
    public String TextoHp()
    {
     return "HP:"+Integer.toString(vida);   
    }
    public String TextoAmmo()
    {
        return Integer.toString(paint)+"/"+Integer.toString(balas);
    }
    public String TextoChave()
    {
        if(chaves<3)
        {
        return "Chaves:"+Integer.toString(chaves)+" / 3";
        }
        else
        {
            return "Chaves: 3 / 3 - Porta Aberta";
        }
    }
    public boolean Morte(int dan)
    {
        if(vida-dan<=0)
            return true;
        else
            return false;
                    
    }
            
}
