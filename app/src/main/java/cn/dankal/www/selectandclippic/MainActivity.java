package cn.dankal.www.selectandclippic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    ImageView iv_thumb;

    Toolbar toolbar;

    private final int FILE_REQUEST_CODE_PICTURE = 1;

    private final int TAKE_PICTURE = 2;

    CropPicUtil cropPicUtil;

    private static final String TAG = "MainActivity";

    //拍照后保存的图片路径
    String takePotoPath = "";
    //裁剪的图片路径
    String CropPicPath = "";
    //图库选择的图片路径
    String selectImagePath = "";

    Bitmap sceneThumb;

    Uri tempUri;

    Uri takePotoUri;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int REQUEST_EXTERNAL_STORAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerView();
        initToolbar();
        init();
    }

    private void registerView() {
        iv_thumb = (ImageView) findViewById(R.id.iv_thumb);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(this);
    }

    private void init() {
        cropPicUtil = new CropPicUtil(this);
        takePotoPath = getExternalCacheDir().getPath()+"/" + "t" + System.currentTimeMillis() + ".jpg";
        takePotoUri = Uri.fromFile(new File(takePotoPath));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (FILE_REQUEST_CODE_PICTURE == requestCode && data != null && resultCode == RESULT_OK) {
            CropPicPath = getExternalCacheDir().getPath()+"/" + System.currentTimeMillis() + ".jpg";
            Uri selectImageUri = data.getData();
            //拿到相册图片后进入裁剪
            if (selectImageUri != null) {
                //将选取的图片uri转化为path
                selectImagePath = uri2path(selectImageUri);
                //复制一份到新的路径
                FileUtil.copyFile(selectImagePath, CropPicPath);
                //将截取的图片路径转化为uri
                tempUri = Uri.fromFile(new File(CropPicPath));
                //带入截图
                cropPicUtil.startBigPhotoCrop(tempUri, 240, 240);
            } else {
                Toast.makeText(getApplicationContext(), "没有选择文件", Toast.LENGTH_SHORT).show();
            }
        } else if (TAKE_PICTURE == requestCode && resultCode == RESULT_OK) {
            CropPicPath = getExternalCacheDir().getPath()+"/" + System.currentTimeMillis() + ".jpg";
            tempUri = Uri.fromFile(new File(CropPicPath));
            FileUtil.copyFile(takePotoPath, CropPicPath);
            cropPicUtil.startBigPhotoCrop(tempUri, 240, 240);
        } else if (CropPicUtil.PICTURE_CUT == requestCode && data != null && resultCode == -1) {//裁剪成功后返回
            sceneThumb = null;
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                sceneThumb = bundle.getParcelable("data");
            }
            Log.e(TAG, "is sceneThumb null?" + (sceneThumb == null));
            if (sceneThumb == null) {
                try {
                    sceneThumb = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tempUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onActivityResult: " + e);
                }
            }
            if (sceneThumb != null) {
                iv_thumb.setImageBitmap(sceneThumb);
            }
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    private String uri2path(Uri uri) {
        String path = "";
        path = SelePicUtil.getImageAbsolutePath(this, uri);
        Log.e(TAG, "uri2path: " + path);
        return path;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.take_poto:
                verifyStoragePermissions(this);
                Log.e(TAG, "onMenuItemClick:take_poto");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, takePotoUri);
                startActivityForResult(intent, TAKE_PICTURE);
                Toast.makeText(MainActivity.this, "拍照", Toast.LENGTH_SHORT).show();
                break;
            case R.id.poto:
                verifyStoragePermissions(this);

                Log.e(TAG, "onMenuItemClick:poto");
                /**
                 * 4.4或以上版本判断
                 */
                boolean isKitKatO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                Intent getAlbum;
//                if (isKitKatO) {
//                    getAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                } else {
//                    getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//                }

                getAlbum = new Intent(Intent.ACTION_PICK);
                getAlbum.setType("image/*");
                startActivityForResult(getAlbum, FILE_REQUEST_CODE_PICTURE);
                break;
        }
        return false;
    }
}
