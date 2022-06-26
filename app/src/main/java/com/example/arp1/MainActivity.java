package com.example.arp1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ModelRenderable modelRenderable;
    private String uri = "https://github.com/CZSheng/3Dmodel/blob/main/Pointing%20Arrow.glb?raw=true";
    private String bulleturi = "https://github.com/CZSheng/3Dmodel/blob/main/Bullet.glb?raw=true";

    private Scene scene;
    private Camera camera;
    private ModelRenderable BulletModelRenderable;
    private android.graphics.Point point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        //Display display = getDisplay();
        point = new Point();
        display.getRealSize(point);

        MyArFragment fragment = (MyArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_1);
        scene = fragment.getArSceneView().getScene();
        camera = scene.getCamera();

        setghost();
        buildbullet();


        Button talk = findViewById(R.id.talk);
        talk.setOnClickListener(view -> {
            shoot();
        });
//        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_1);
//        setupmodel();
//        setupplane();
        /*arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();

            MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                    .thenAccept(material -> {
                        modelRenderable = ShapeFactory.makeSphere(1.0f,new Vector3(0f,1f,1f),material);

                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setRenderable(modelRenderable);
                        arFragment.getArSceneView().getScene().addChild(anchorNode);

                    });
        });*/

    }



    private void setghost() {
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(this,
                        Uri.parse(uri),
                        RenderableSource.SourceType.GLB)
                        .setScale(0.75f)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(uri)
                .build()
                .thenAccept(modelRenderable1 -> {
                    int x,y,z;

                    for(int i = 0; i < 1; i++){
                        Node node = new Node();
                        node.setRenderable(modelRenderable1);


                        //randomly generate balloons on the scene
                        Random random = new Random();
                        x = random.nextInt(10);
                        y = 0;
                        z = random.nextInt(10);

                        z = -z;



                        Vector3 ghostposition = new Vector3( (float) x,  y,  (float) z);
                        node.setWorldPosition(ghostposition);
                        scene.addChild(node);

                    }
                });
    }

    private void buildbullet() {
        Texture
                .builder()
                .setSource(this, R.drawable.texture)
                .build()
                .thenAccept(texture -> {
                    MaterialFactory
                            .makeOpaqueWithTexture(this, texture)
                            .thenAccept(material -> {

                                BulletModelRenderable = ShapeFactory
                                        .makeSphere(0.001f,
                                                new Vector3(0f,0f, 0f), // centre of bullet
                                                material);
                            });
                });

    }

    private void shoot() {
        Ray ray = camera.screenPointToRay(point.x/2f, point.y/2f);
        Node node = new Node();
        node.setRenderable(BulletModelRenderable);
        scene.addChild(node);


        new Thread(()->{
            for(int i = 0; i<2000;i++){
                int finali = i;
                runOnUiThread(()->{
                    Vector3 v = ray.getPoint(finali*0.1f);
                    node.setWorldPosition(v);

                    Node nodecontact = scene.overlapTest(node);
                    if(nodecontact!= null){
                        scene.removeChild(nodecontact);
                        Talkdialog1 talkdialog1 = new Talkdialog1();
                        talkdialog1.show(getSupportFragmentManager(),"talk dialog 1");
                    }



                });
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        runOnUiThread(()->{
            scene.removeChild(node);
        });

        }).start();
    }


//    private void setupmodel() {
//        ModelRenderable.builder()
//                .setSource(this, RenderableSource.builder().setSource(this,
//                        Uri.parse(uri),
//                        RenderableSource.SourceType.GLB)
//                        .setScale(0.75f)
//                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
//                        .build())
//                .setRegistryId(uri)
//                .build()
//                .thenAccept(modelRenderable1 -> modelRenderable = modelRenderable1)
//                .exceptionally(throwable ->{
//                            Toast.makeText(this, "model cannot be load", Toast.LENGTH_SHORT).show();
//                            return null;
//                });
//
//    }
//
//
//    private void setupplane() {
//
//        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
//            @Override
//            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
//                Anchor anchor = hitResult.createAnchor();
//                AnchorNode anchorNode = new AnchorNode(anchor);
//                anchorNode.setParent(arFragment.getArSceneView().getScene());
//                createModel(anchorNode);
//            }
//        });
//    }
//
//    private void createModel(AnchorNode anchorNode) {
//
//        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
//        node.setParent(anchorNode);
//        node.setRenderable(modelRenderable);
//        node.select();
//    }


}