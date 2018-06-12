/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 *
 * @author 152728
 */
public class LabGrafo {

    int m[][], dimensao,count_inimigo=0;
    int i_in, j_in, tamanho, criado, chave = 0, municao = 0, vida = 0, chavemax, municaomax, vidamax;
    int rand_val = 0, rand_count = 0;
    
    public int[][] Gerar() {
           int controle_chave=0;
        LabNod no, atual, novo;
        List<LabNod> vizinhos;
        Stack pilha = new Stack(), pilha_aux = new Stack();
        int controle_min;
        Random a = new Random(), b;
        dimensao = 50 + a.nextInt(20);
        m = new int[dimensao][dimensao];
        for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                m[i][j] = 1;
            }
        }
        a = new Random();
        i_in = 1 + a.nextInt(dimensao - 3);
        a = new Random();
        j_in = 1 + a.nextInt(dimensao - 3);
        m[i_in][j_in] = 3;
        tamanho = (dimensao - 4) / 2;
        criado = 0;
        no = new LabNod(i_in, j_in, 0);
        pilha.push(no);
        atual = no;
        do {
            vizinhos = Vizinhos(atual);
            if (vizinhos.size() > 0) {
                count_inimigo++;
                novo = vizinhos.get(Random(vizinhos));
                criado++;
                pilha.push(novo);
                m[novo.getI()][novo.getJ()] = 0;               
                atual = novo;
            } else {
                do {
                    criado--;
                    if (!pilha.isEmpty()) {
                        atual = (LabNod) pilha.pop();
                        vizinhos = Vizinhos(atual);
                    } else {
                        break;
                    }

                } while (vizinhos.size() == 0);
                if (vizinhos.size() > 0) {
                    a = new Random();
                    novo = vizinhos.get(Random(vizinhos));
                    criado++;
                    pilha.push(novo);
                    m[novo.getI()][novo.getJ()] = 0;
                    atual = novo;
                }               
            }
            count_inimigo++;
            if(count_inimigo>15)
            {
                m[atual.getI()][atual.getJ()]=6;
                count_inimigo=0;
            }
        } while (criado <= tamanho);
        
        atual = (LabNod) pilha.pop();
        m[atual.getI()][atual.getJ()] = 9;
        atual=(LabNod)pilha.pop();
        m[atual.getI()][atual.getJ()]=7;
        atual = (LabNod) pilha.pop();
        m[atual.getI()][atual.getJ()] = 8;
        int controle = 0, para = 0;
        if (dimensao < 60) {
            controle_min = 10;
            chavemax = 3;
            municaomax = 4;
            vidamax = 3;

        } else if (dimensao < 80) {
            controle_min = 15;
            chavemax = 3;
            municaomax = 5;
            vidamax = 5;
        } else {
            controle_min = 20;
            chavemax = 3;
            municaomax = 7;
            vidamax = 6;
        }
        
        do {
            a = new Random();
            int ramo = a.nextInt(2);

            if (ramo == 2) {
                pilha_aux.push(pilha.pop());
            } else {
                if (controle < controle_min) {
                    atual=(LabNod) pilha.pop();
                    pilha_aux.push(atual);
                   if(Ramos(atual))
                   {
                      controle++; 
                   }
                   
                } else {
                    pilha.pop();
                }
                
            }
            if (pilha.isEmpty()) {
                pilha = pilha_aux;
                pilha_aux = new Stack();
                para++;
            }
        } while (controle < controle_min && para < 10);
        while(chave<chavemax && controle_chave<5)
        {
            for (int i = 0; i < dimensao; i++) {
            for (int j = 0; j < dimensao; j++) {
                if(m[i][j] == 2 || m[i][j]==4)
                {
                    m[i][j]=5;
                    chave++;
                    i=dimensao+1;
                    j=dimensao+1;
                }
            }
            controle_chave++;
        }
        }
        if(chave<chavemax)
        {
            if(!pilha.isEmpty())
            {
                atual= (LabNod)pilha.pop();
                m[atual.getI()][atual.getJ()]=5;
                chave++;
            }
            else if(pilha_aux.isEmpty())
            {
                atual= (LabNod)pilha_aux.pop();
                m[atual.getI()][atual.getJ()]=5;
                chave++;
            }
            else
            {
                chave++; 
            }
        }
        return m;
    }

    public boolean Ramos(LabNod inicio) {
        Random a;
        int tipo, tamanho;
        LabNod no, atual, novo;
        List<LabNod> vizinhos;
        Stack pilha = new Stack();
        a = new Random();
        tipo = a.nextInt(3);
        atual = inicio;
        criado = 0;
        count_inimigo=0;
                
        if (tipo == 1) {
            tamanho = 10;
        } else if (tipo == 2) {
            tamanho = 13;
        } else {
            tamanho = 15;
        }
        do {
            vizinhos = Vizinhos(atual);
            if (vizinhos.size() > 0) {

                novo = vizinhos.get(Random(vizinhos));
                criado++;
                pilha.push(novo);
                m[novo.getI()][novo.getJ()] = 0;
                atual = novo;
            } else {
                do {
                    criado--;
                    if (!pilha.isEmpty()) {
                        atual = (LabNod) pilha.pop();
                        vizinhos = Vizinhos(atual);
                    } else {
                        break;
                    }

                } while (vizinhos.size() == 0 && !pilha.isEmpty());
                if (vizinhos.size() > 0) {
                    a = new Random();
                    novo = vizinhos.get(Random(vizinhos));
                    criado++;
                    pilha.push(novo);
                    m[novo.getI()][novo.getJ()] = 0;
                    atual = novo;
                }
            }
            count_inimigo++;
            if(count_inimigo>=7)
            {
                m[atual.getI()][atual.getJ()]=6;
                count_inimigo=0;
            }
        } while (criado <= tamanho && !pilha.isEmpty());
        if(criado>=7)
        {
        m[atual.getI()][atual.getJ()]= ItemRandom();  
        return true;
        }
        return false;
           
    }

    public List<LabNod> Vizinhos(LabNod no) {
        List<LabNod> lista = new ArrayList<LabNod>();
        LabNod novo;
        int i, j;
        i = no.getI();
        j = no.getJ();
        if (i - 1 > 1 && m[i - 1][j] == 1 && m[i - 1][j - 1] == 1 && m[i - 1][j + 1] == 1) {
            if ((i - 2 < 1) || (i - 2 > 1 && m[i - 2][j] == 1)) {
                novo = new LabNod(i - 1, j, no.getCont() + 1, no);
                lista.add(novo);
            }
        }
        if (i + 1 < dimensao - 2 && m[i + 1][j] == 1 && m[i + 1][j - 1] == 1 && m[i + 1][j + 1] == 1) {
            if ((i + 2 > dimensao - 2) || (i + 2 < dimensao - 2 && m[i + 2][j] == 1)) {
                novo = new LabNod(i + 1, j, no.getCont() + 1, no);
                lista.add(novo);
            }
        }
        if (j - 1 > 1 && m[i][j - 1] == 1 && m[i - 1][j - 1] == 1 && m[i + 1][j - 1] == 1) {
            if ((j - 2 < 1) || (j - 2 > 1 && m[i][j - 2] == 1)) {
                novo = new LabNod(i, j - 1, no.getCont() + 1, no);
                lista.add(novo);
            }
        }
        if (j + 1 < dimensao - 2 && m[i][j + 1] == 1 && m[i - 1][j + 1] == 1 && m[i + 1][j + 1] == 1) {
            if ((j + 2 > dimensao - 2) || (j - 2 < dimensao - 2 && m[i][j + 2] == 1)) {
                novo = new LabNod(i, j + 1, no.getCont() + 1, no);
                lista.add(novo);
            }
        }

        return lista;
    }

    public int Random(List<LabNod> vizinhos) {
        Random a = new Random();
        int num;

        num = a.nextInt(vizinhos.size());
        if (num == vizinhos.size()) {
            num--;
        }
        return num;
    }

    public int ItemRandom() {
        boolean valida = true;
        int item_cod,retorno=2;
        Random item;
        item = new Random();
        item_cod = item.nextInt(3);

        do {
            if (item_cod == 1) {
                if (municao < municaomax) {
                    municao++;
                    valida = false;
                    retorno=2;
                } else {
                    item_cod = 2;
                }
            } else if (item_cod == 2) {
                if (vida < vidamax) {
                    vida++;
                    valida = false;
                    retorno=4;
                } else {
                    item_cod = 3;
                }
            }
            else
            {
                if (chave < chavemax) {
                    chave++;
                    valida = false;
                    retorno=5;
                } else {
                    item_cod = 1;
                } 
            }
            if(chave >= chavemax && municao >= municaomax && vida >= vidamax)
            {
                valida=false;
                item_cod=0;
            }
        } while (valida);

        return retorno;
    }
}
