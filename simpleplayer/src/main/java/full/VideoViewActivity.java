package full;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simpleplayer.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class VideoViewActivity extends AppCompatActivity {

    //----------------启动当前的Activity---------------
    private static final String KEY_VIDEO_PATH = "video_path";

    public static void open(Context context, String videoPath){
        Intent intent = new Intent(context,VideoViewActivity.class);
        intent.putExtra(KEY_VIDEO_PATH,videoPath);
        context.startActivity(intent);
    }
    //------------------启动当前的Activity---------------------

    private VideoView videoView;
    private ImageView ivLoading;//缓冲信息（图像）
    private TextView tvBufferInfo;//缓冲信息（233kb/s 35%）
    private MediaPlayer mediaPlayer;
    private int bufferPercent;//缓冲百分比
    private int downloadSpeed;//下载速度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置背景色
        getWindow().setBackgroundDrawableResource(android.R.color.black);
        //设置当前内容视图
        setContentView(R.layout.activity_video_view);

        //初始化缓冲相关视图
        initBufferView();
        //初始化VideoView
        initVideoView();
    }

    //设置数据源
    @Override
    protected void onResume() {
        super.onResume();
        videoView.setVideoPath(getIntent().getStringExtra(KEY_VIDEO_PATH));
    }

    //停止播放
    @Override
    protected void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }

    //初始化缓冲相关视图
    private void initBufferView(){
        tvBufferInfo = (TextView) findViewById(R.id.tvBufferInfo);
        ivLoading = (ImageView) findViewById(R.id.ivLoading);
        tvBufferInfo.setVisibility(View.INVISIBLE);
        ivLoading.setVisibility(View.INVISIBLE);
    }

    //初始化VideoView
    private void initVideoView(){
        Vitamio.isInitialized(this);
        videoView = (VideoView) findViewById(R.id.videoView);
        //自定义控制器
        videoView.setMediaController(new CustomMediaController(this));
        videoView.setKeepScreenOn(true);//设置屏幕常亮
        videoView.requestFocus();//拿到焦点
        //缓冲第一步，准备监听中，设置缓冲大小
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //拿到Mediaplayer
                mediaPlayer = mp;
                //设置缓冲大小
                mediaPlayer.setBufferSize(1024 * 512);
            }
        });
        //缓冲第二步，信息监听中，监听缓冲状态，拿到缓冲速度
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    //缓冲开始
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        showBufferView();
                        if (videoView.isPlaying()){
                            videoView.pause();
                        }
                        break;
                    //缓冲结束
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        hideBufferView();
                        videoView.start();
                        break;
                    //缓冲速率
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        downloadSpeed = extra;
                        upDateBufferView();
                        break;
                }
                return false;
            }
        });
        //缓冲第三步，缓冲更新监听中，拿到缓冲百分比
        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                bufferPercent = percent;
                upDateBufferView();
            }
        });
    }

    //显示缓冲视图
    private void showBufferView(){
        tvBufferInfo.setVisibility(View.VISIBLE);
        ivLoading.setVisibility(View.VISIBLE);
        downloadSpeed = 0;
        bufferPercent = 0;
    }

    //隐藏缓冲视图
    private void hideBufferView(){
        tvBufferInfo.setVisibility(View.INVISIBLE);
        ivLoading.setVisibility(View.INVISIBLE);
    }

    //更新缓冲视图UI
    private void upDateBufferView(){
        String info = bufferPercent + "%  " + downloadSpeed + "kb/s";
        tvBufferInfo.setText(info);
    }
}
