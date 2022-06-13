package com.example.arp1;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ModelRenderable modelRenderable;
    private String uri = "https://github.com/CZSheng/3Dmodel/blob/main/Pointing%20Arrow.glb?raw=true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_1);
        setupmodel();
        setupplane();
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


    private void setupmodel() {
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(this,
                        Uri.parse(uri),
                        RenderableSource.SourceType.GLB)
                        .setScale(0.75f)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(uri)
                .build()
                .thenAccept(modelRenderable1 -> modelRenderable = modelRenderable1)
                .exceptionally(throwable ->{
                            Toast.makeText(this, "model cannot be load", Toast.LENGTH_SHORT).show();
                            return null;
                });

    }


    private void setupplane() {

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());
                createModel(anchorNode);
            }
        });
    }

    private void createModel(AnchorNode anchorNode) {

        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setRenderable(modelRenderable);
        node.select();
    }


}