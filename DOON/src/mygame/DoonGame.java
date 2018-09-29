package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.Random;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoonGame
        extends SimpleApplication
        implements ActionListener,
        PhysicsCollisionListener {
    Map<String,Inimigo> inimigo ;
    Player jogador;
    int matriz[][];
    Vector3f ini;
    Vector<Geometry> rotacao;
    private Sphere bullet;
    Material matBullet;
    float bulletSize = 0.3f;
    private SphereCollisionShape bulletCollisionShape;
    BitmapText vida, balas_disponiveis, chaves_text,menu_text,fase;
    boolean portao = false;
    int menu=0,count=1,countanterior=1;
    Spatial menuArea;
    public static void main(String[] args) {
        DoonGame app = new DoonGame();
        app.showSettings = false;
        app.start();
    }
    private BulletAppState bulletAppState;
    private PlayerCameraNode player;
    private boolean up = false, down = false, left = false, right = false, shoot = false, reload = false,space_bar=false;
    private Material boxMatColosion;
    private int inimigo_count;
    @Override
    public void simpleInitApp() {
        jogador = new Player();
        rotacao = new Vector<Geometry>();
        inimigo_count=0;
        inimigo= new HashMap<>();
        SetFase();
        bulletAppState = new BulletAppState();
        setDisplayFps(false);
        setDisplayStatView(false);

        stateManager.attach(bulletAppState);
        bullet = new Sphere(32, 32, 1.0f, true, false);
        bullet.setTextureMode(Sphere.TextureMode.Projected);

        createLigth();
        boxMatColosion = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        boxMatColosion.setBoolean("UseMaterialColors", true);
        boxMatColosion.setColor("Ambient", ColorRGBA.Red);
        boxMatColosion.setColor("Diffuse", ColorRGBA.Red);


        initKeys();
        initMenuLoad();
        bulletAppState.setDebugEnabled(false);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        matBullet = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        matBullet.setTexture("ColorMap", tex2);
        
    }
    public void Inimigo_Tiro(float tpf)
    {
      List<Inimigo> list = new ArrayList<Inimigo>(inimigo.values());
      Vector3f vetor;
      float x,z,aj_x,aj_z;
      for(int i=0;i<list.size();i++)
      {
          Inimigo aux= list.get(i);
          aux.Acoes(tpf);
          
          if(aux.Atirar_Valido())
          {
        aux.Atirar();
        
        x= aux.getInimigo().getLocalTranslation().x- player.getCamLook().getX();
        
        z= aux.getInimigo().getLocalTranslation().z-player.getCamLook().getZ();
        if(x>=0)
        {
           aj_x=-1.2f;
        }
        else
        {
          aj_x=1.2f;  
        }
        if(z>=0)
        {
          aj_z=-1.2f;  
        }
        else
        {
          aj_z=1.2f;  
        }
        if(Math.abs(x)<2.5f)
        {
            aj_x=0;
            
        }
        if(Math.abs(z)<2.5f)
        {
            aj_z=0;
            
        }
        Geometry bulletg = new Geometry("bullet_ini", bullet);
        bulletg.setMaterial(matBullet);
        bulletg.setLocalTranslation(aux.getInimigo().getLocalTranslation().x+aj_x, aux.getInimigo().getLocalTranslation().y + 0.3f , aux.getInimigo().getLocalTranslation().z+aj_z);
        System.out.println(bulletg.getLocalTranslation());
        bulletg.setLocalScale(bulletSize);
        bulletCollisionShape = new SphereCollisionShape(bulletSize);
        BombControl bulletNode = new BombControl(assetManager, bulletCollisionShape, 1);
        bulletNode.setForceFactor(8);
        bulletNode.setExplosionRadius(20);
        bulletNode.setCcdMotionThreshold(0.001f);
        vetor = new Vector3f(-player.getViewDirection().x,player.getViewDirection().y,-player.getViewDirection().z);
        bulletNode.setLinearVelocity(vetor.mult(50));
        bulletg.addControl(bulletNode);
        rootNode.attachChild(bulletg);
        getPhysicsSpace().add(bulletNode);
        }
      }
      
    }
    public void Reset()
    {
        if(count<=3 && count>=1)
        {           
            rootNode.detachAllChildren();
            inimigo_count=0;
            inimigo= new HashMap<>();
            stateManager.detach(bulletAppState);
            bulletAppState = new BulletAppState();
            stateManager.attach(bulletAppState);
            SetFase();
            createLab();
            createPlayer();
            initKeys();
            bulletAppState.setDebugEnabled(false);
            bulletAppState.getPhysicsSpace().addCollisionListener(this);
            jogador = new Player();
            jogador.vida=100;
            vida.setText(jogador.TextoHp());
            balas_disponiveis.setText(jogador.TextoAmmo());
            chaves_text.setText(jogador.TextoChave());
            fase.setText("Nvl:"+Integer.toString(count));
            countanterior=count;
        }
        else if(count == -1)
        {
            rootNode.detachAllChildren();
            menu=-1;
            vida.setText("");
            balas_disponiveis.setText("");
            chaves_text.setText("");
            fase.setText("");
            menu_text.setText("\tComandos:\r\n\r\nJ-Atira\r\n\r\nR-Recarrega\r\n\r\nW-Movimenta Para Cima\r\n\r\nA-Movimenta Para Esquerda\r\n\r\nS-Movimenta Para Atrás\r\n\r\nD-Movimenta Para Direita\r\n\r\n\r\n\r\nGame Over\n\r\nPrecione Enter para Jogar Novamente");
        }
        else
        {
            rootNode.detachAllChildren();
            menu= -1;
            vida.setText("");
            balas_disponiveis.setText("");
            chaves_text.setText("");
            fase.setText("");
            menu_text.setText("\tComandos:\r\n\r\nJ-Atira\r\n\r\nR-Recarrega\r\n\r\nW-Movimenta Para Cima\r\n\r\nA-Movimenta Para Esquerda\r\n\r\nS-Movimenta Para Atrás\r\n\r\nD-Movimenta Para Direita\r\n\r\n\r\n\r\nParabens voce terminou os tres andares do labirinto\n\r\nPrecione Enter para Jogar Novamente");
        }
    
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(menu==1)
        {
        initCrossHairs();
        player.upDateKeys(tpf, up, down, left, right);
        if (shoot && !jogador.tiro_delay && !jogador.tiro_recarga && jogador.TemBalaPaint()) {
            TiroInstancia();
        } else if ((shoot && !jogador.tiro_delay && !jogador.tiro_recarga && !jogador.TemBalaPaint()) || reload) {
            TiroRecarregar();
        }
        jogador.Acoes(tpf);
        Rotacionar(tpf);
        PortaoAbrir();
        Inimigo_Tiro(tpf);
        }
        else if(menu==0)
        {
            if(space_bar)
            {
            createLab();
            createPlayer();
            menu_text.setText("");
            initMenu();
            menu=1;
            }

        }
        else if(menu==-1)
        {
            if(space_bar)
            {
            menu_text.setText("");
            menu=1;
            count=1;
            countanterior=1;
            Reset();
            }
        }    
            
    }

    private void PortaoAbrir() {
        if (jogador.ChavePega() && !portao) {
            Spatial s = rootNode.getChild("door");
            bulletAppState.getPhysicsSpace().remove(s);
            rootNode.detachChild(s);
            portao = true;
        }
    }

    private void TiroRecarregar() {
        if (jogador.TemBala()) {
            jogador.Recarregar();
            balas_disponiveis.setText(jogador.TextoAmmo());
        }
    }

    private void TiroInstancia() {

        jogador.Atirar();
        balas_disponiveis.setText(jogador.TextoAmmo());
        Geometry bulletg = new Geometry("bullet", bullet);
        bulletg.setMaterial(matBullet);
        bulletg.setLocalTranslation(player.getCamLook().getX(), player.getCamLook().getY() + 0.3f, player.getCamLook().getZ());
        System.out.println(bulletg.getLocalTranslation());
        bulletg.setLocalScale(bulletSize);
        bulletCollisionShape = new SphereCollisionShape(bulletSize);
        BombControl bulletNode = new BombControl(assetManager, bulletCollisionShape, 1);
        bulletNode.setForceFactor(8);
        bulletNode.setExplosionRadius(20);
        bulletNode.setCcdMotionThreshold(0.001f);
        bulletNode.setLinearVelocity(player.getViewDirection().mult(50));
        bulletg.addControl(bulletNode);
        rootNode.attachChild(bulletg);
        getPhysicsSpace().add(bulletNode);
    }

    private void Rotacionar(float tpf) {
        for (int i = 0; i < rotacao.size(); i++) {
            rotacao.get(i).rotate(0, tpf, 0);
        }
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    protected void initCrossHairs() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 1.80f + ch.getLineHeight() / 1.80f, 0);
        guiNode.attachChild(ch);
    }

    protected void initMenu() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        vida = new BitmapText(guiFont, false);
        balas_disponiveis = new BitmapText(guiFont, false);
        fase = new BitmapText(guiFont, false);
        fase.setText("Nvl:"+Integer.toString(count));
        vida.setText(jogador.TextoHp());
        balas_disponiveis.setText(jogador.TextoAmmo());
        vida.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 6.0f + vida.getLineHeight() / 6.0f, 0);
        balas_disponiveis.setLocalTranslation( // center
                settings.getWidth() / 1.15f - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 6.0f + balas_disponiveis.getLineHeight() / 6.0f, 0);

        chaves_text = new BitmapText(guiFont, false);
        chaves_text.setText(jogador.TextoChave());
        chaves_text.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 1.01f + chaves_text.getLineHeight() / 6.0f, 0);
        
        fase.setLocalTranslation( // center
                settings.getWidth() / 7 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 1.01f + fase.getLineHeight() / 6.0f, 0);
        
        
        guiNode.attachChild(vida);
        guiNode.attachChild(balas_disponiveis);
        guiNode.attachChild(chaves_text);
        guiNode.attachChild(fase);
    }
        protected void initMenuLoad() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        menu_text = new BitmapText(guiFont, false);
        menu_text.setText("\tComandos:\r\n\r\nJ-Atira\r\n\r\nR-Recarrega\r\n\r\nW-Movimenta Para Cima\r\n\r\nA-Movimenta Para Esquerda\r\n\r\nS-Movimenta Para Atrás\r\n\r\nD-Movimenta Para Direita\r\n\r\n\r\n\r\nPrecione Enter para Iniciar");

        menu_text.setLocalTranslation( // center
                settings.getWidth() / 2.5f - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 1.1f + menu_text.getLineHeight() / 1.3f, 0);
       
      
        guiNode.attachChild(menu_text);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void createPlayer() {

        player = new PlayerCameraNode("player", assetManager, bulletAppState, cam, ini);
        rootNode.attachChild(player);
        flyCam.setEnabled(false);

    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        switch (binding) {
            case "CharLeft":
                if (value) {
                    left = true;
                } else {
                    left = false;
                }
                break;
            case "CharRight":
                if (value) {
                    right = true;
                } else {
                    right = false;
                }
                break;
        }
        switch (binding) {
            case "CharForward":
                if (value) {
                    up = true;
                } else {
                    up = false;
                }
                break;
            case "CharBackward":
                if (value) {
                    down = true;
                } else {
                    down = false;
                }
                break;
        }
        switch (binding) {
            case "Shoot":
                if (value) {
                    shoot = true;
                } else {
                    shoot = false;
                }
                break;

        }
        switch (binding) {
            case "Begin":
                if (value) {
                    space_bar = true;
                } else {
                    space_bar = false;
                }
                break;
        }
                switch (binding) {
            case "Reload":
                if (value) {
                    reload = true;
                } else {
                    reload = false;
                }
                break;
        }
        
    }

    private void createLigth() {

        DirectionalLight l1 = new DirectionalLight();
        l1.setDirection(new Vector3f(1, -0.7f, 0));
        rootNode.addLight(l1);

        DirectionalLight l2 = new DirectionalLight();
        l2.setDirection(new Vector3f(-1, 0, 0));
        rootNode.addLight(l2);

        DirectionalLight l3 = new DirectionalLight();
        l3.setDirection(new Vector3f(0, 0, -1.0f));
        rootNode.addLight(l3);

        DirectionalLight l4 = new DirectionalLight();
        l4.setDirection(new Vector3f(0, 0, 1.0f));
        rootNode.addLight(l4);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

    }
    private void createMenu(float x, float y, float z, String tex, String est) {
        /* A colored lit cube. Needs light source! */
        Box boxMesh = new Box(500f, 500f, 500f);
        Geometry boxGeo = new Geometry("Menu", boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/" + tex + "." + est);
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);

        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(x, y, z);
        rootNode.attachChild(boxGeo);
        menuArea = boxGeo;
    }
    private void createCubo(float x, float y, float z, String tex, String est) {
        /* A colored lit cube. Needs light source! */
        Box boxMesh = new Box(3f, 3f, 3f);
        Geometry boxGeo = new Geometry(tex, boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/" + tex + "." + est);
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);

        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(x, y, z);
        rootNode.attachChild(boxGeo);

        RigidBodyControl boxPhysicsNode = new RigidBodyControl(0);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);

    }
    
     private void createTeto(float x, float y, float z) {
        Box boxMesh = new Box((matriz.length+5)*6, 1f, (matriz.length+5)*6);
        Geometry boxGeo = new Geometry("teto", boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/teto.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);

        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(x, y, z);
        rootNode.attachChild(boxGeo);

        RigidBodyControl boxPhysicsNode = new RigidBodyControl(0);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);

    }
    
    private void createFim(float x, float y, float z) {

        Box boxMesh = new Box(1.5f, 3f, 1.5f);
        Geometry boxGeo = new Geometry("fim", boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/fim.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);

        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(x, y, z);
        rootNode.attachChild(boxGeo);

        RigidBodyControl boxPhysicsNode = new RigidBodyControl(0);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);

    }

    private void createInimigo(float x, float y, float z, String nome) {
        /* A colored lit cube. Needs light source! */
        Box boxMesh = new Box(0.8f, 0.8f, 0.8f);
        Geometry boxGeo = new Geometry(nome, boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/rob.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);

        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(x, y, z);
        rootNode.attachChild(boxGeo);

        RigidBodyControl boxPhysicsNode = new RigidBodyControl(0);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);
        inimigo.put(nome, new Inimigo(boxGeo));
        inimigo_count++;
    }
    
    private void createLab() {
        int i, j,metade;
        for (i = 0; i < matriz.length; i++) {
            for (j = 0; j < matriz[0].length; j++) {
                if (matriz[i][j] != 1 && matriz[i][j] != 9 && matriz[i][j] != 8 && matriz[i][j] != 7) {
                    createCubo(i * 6, 0, j * 6, "tex", "png");
                }
                if (matriz[i][j] == 9 || matriz[i][j] == 8 || matriz[i][j] == 7) {
                    createCubo(i * 6, 0, j * 6, "scifi", "jpg");
                }
                if (matriz[i][j] == 1) {
                    createCubo(i * 6, 5, j * 6, "tex", "png");
                }
                if (matriz[i][j] == 3) {
                    ini = new Vector3f(i * 6, 2, j * 6);
                }
                if (matriz[i][j] == 2) {
                    criarItem(i * 6f, 4.7f, j * 6, "ammo");
                }
                if (matriz[i][j] == 4) {
                    criarItem(i * 6f, 4.7f, j * 6, "hp");
                }
                if (matriz[i][j] == 5) {
                    criarChave(i * 6f, 4.7f, j * 6);
                }
                if(matriz[i][j] == 6)
                {
                    createInimigo(i * 6, 4.5f, j * 6,"inimigo"+Integer.toString(inimigo_count));
                }
                if (matriz[i][j] == 8) {
                    createCubo(i * 6, 5, j * 6, "door","png");
                }
                if (matriz[i][j] == 9) {
                    createFim(i * 6, 6, j * 6);
                }
            }
        }
        metade = (matriz.length)/2;    
        createTeto(metade, 8.5f, metade);
    }

    public void criarChave(float x, float y, float z) {

        Box boxMesh = new Box(0.7f, 1.1f, 0.01f);
        Geometry boxGeo = new Geometry("chave", boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/chave.png");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(x, y, z);
        rootNode.attachChild(boxGeo);
        rotacao.add(boxGeo);

        RigidBodyControl boxPhysicsNode = new RigidBodyControl(0);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);

    }

    public void criarItem(float x, float y, float z, String name) {

        Box boxMesh = new Box(1f, 0.5f, 0.5f);
        Geometry boxGeo = new Geometry(name, boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/" + name + ".jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(x, y, z);
        rootNode.attachChild(boxGeo);
        rotacao.add(boxGeo);

        RigidBodyControl boxPhysicsNode = new RigidBodyControl(0);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);

    }

    private void initKeys() {
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharBackward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Reload", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Begin", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "CharLeft", "CharRight");
        inputManager.addListener(this, "CharForward", "CharBackward");
        inputManager.addListener(this, "Shoot", "Shoot");
        inputManager.addListener(this, "Reload", "Reload");
        inputManager.addListener(this, "Begin", "Begin");

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {

        if (event.getNodeA().getName().equals("player")
                || event.getNodeB().getName().equals("player")) {

            if (event.getNodeA().getName().equals("ammo")) {
                Spatial s = event.getNodeA();
                bulletAppState.getPhysicsSpace().remove(s);
                rootNode.detachChild(s);
                jogador.AddAmmo(15);
                balas_disponiveis.setText(jogador.TextoAmmo());

            } else if (event.getNodeB().getName().equals("ammo")) {
                Spatial s = event.getNodeB();
                bulletAppState.getPhysicsSpace().remove(s);
                rootNode.detachChild(s);
                jogador.AddAmmo(15);
                balas_disponiveis.setText(jogador.TextoAmmo());
            }
            if (event.getNodeA().getName().equals("hp")) {
                Spatial s = event.getNodeA();
                bulletAppState.getPhysicsSpace().remove(s);
                rootNode.detachChild(s);
                jogador.AddVida(35);
                vida.setText(jogador.TextoHp());

            } else if (event.getNodeB().getName().equals("hp")) {
                Spatial s = event.getNodeB();
                bulletAppState.getPhysicsSpace().remove(s);
                rootNode.detachChild(s);
                jogador.AddVida(35);
                vida.setText(jogador.TextoHp());
            }
            if (event.getNodeB().getName().equals("chave")) {
                if (!jogador.chave_delay) {
                    Spatial s = event.getNodeB();
                    bulletAppState.getPhysicsSpace().remove(s);
                    rootNode.detachChild(s);
                    jogador.AddChave();
                    chaves_text.setText(jogador.TextoChave());
                }
            } else if (event.getNodeA().getName().equals("chave")) {
                {
                    if (!jogador.chave_delay) {
                    Spatial s = event.getNodeA();
                    bulletAppState.getPhysicsSpace().remove(s);
                    rootNode.detachChild(s);
                    jogador.AddChave();
                    chaves_text.setText(jogador.TextoChave());
                    }
                }
            }
            if (event.getNodeB().getName().equals("fim"))
            {
                 if(count==countanterior)
                {
                   count++;
                   Reset();
                }
                   
            }
            else if(event.getNodeA().getName().equals("fim"))
            {    
                if(count==countanterior)
                {
                   count++;
                   Reset();
                }
                   
            }
            if (event.getNodeA().getName().equals("bullet_ini")) {
                if(jogador.Morte(15))
                {
                    if(count==countanterior)
                    {
                        count=-1;
                        countanterior=-1;
                        Reset();
                    }
                   
                }
                else
                {
                 jogador.Dano(15);
                 vida.setText(jogador.TextoHp());
                }

            } else if (event.getNodeB().getName().equals("bullet_ini")) {
                if(jogador.Morte(15))
                {
                    if(count==countanterior)
                    {
                        count=-1;
                        countanterior=-1;
                        Reset();
                    }
                    
                }
                else
                {
                 jogador.Dano(15);
                 vida.setText(jogador.TextoHp());
                }
            }

        }
        if (event.getNodeA().getName().equals("bullet")
                || event.getNodeB().getName().equals("bullet")) {

            if (inimigo.containsKey(event.getNodeA().getName())) {
                Spatial s = event.getNodeA();
                Inimigo target= inimigo.get(event.getNodeA().getName());
                if(target.Morte())
                {
                bulletAppState.getPhysicsSpace().remove(s);
                rootNode.detachChild(s);
                inimigo.remove(event.getNodeA().getName());
                }
                else
                {
                    target.Dano();
                }
            } else if (inimigo.containsKey(event.getNodeB().getName())) {
                Spatial s = event.getNodeB();
                Inimigo target= inimigo.get(event.getNodeB().getName());
                if(target.Morte())
                {
                bulletAppState.getPhysicsSpace().remove(s);
                rootNode.detachChild(s);
                inimigo.remove(event.getNodeB().getName());
                }
                else
                {
                    target.Dano();
                }
            }

        }

    }

    public void SetFase() {

        matriz = new LabGrafo().Gerar();
    }
}
