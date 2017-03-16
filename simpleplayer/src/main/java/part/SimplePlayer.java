package part;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.simpleplayer.R;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * Created by yys on 2017/3/14.
 */

public class SimplePlayer extends FrameLayout {
    //进度条控制
    private static final int PROGRESS_MAX = 1000;
    //视频路径
    private String videoPath;
    private MediaPlayer mediaPlayer;
    //是否准备好
    private boolean isPrepared;
    //是否正在播放
    private boolean isPlaying;

    //视图
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    //预览图
    private ImageView imageView;
    //播放，暂停
    private ImageButton imageButton;
    //进度条
    private ProgressBar progressBar;

    public SimplePlayer(Context context) {
        super(context,null);
    }

    public SimplePlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimplePlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //视图初始化相关
        init();
    }

    private void init() {
        //初始化VItamio
        Vitamio.isInitialized(getContext());
        //填充布局
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player,this,true);
        //初始化SurfaceView
        initSurfaceView();
        //初始化视频播放控制视图
        initControllerViews();
    }

    //设置数据源
    public void setVideoPath(String videoPath){
        this.videoPath = videoPath;
    }
    //初始化状态（在Activity的onResume调用）
    public void onResume(){
        //初始化MediaPlayer
        initMediaPlayer();
        //准备MediaPlayer
        prepareMediaPlayer();
    }
    //释放状态（在Activity的onPause调用）
    public void onPause(){
        //暂停MediaPlayer
        pauseMediaplayer();
        //释放MediaPlayer
        releaseMediaPlayer();
    }
    //初始化SurfaceView
    private void initSurfaceView(){
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        //使用Vitamio要设置像素格式，否则会花屏
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }
    //初始化视频播放控制视图
    private void initControllerViews(){
        imageView = (ImageView) findViewById(R.id.ivPreview);
        imageButton = (ImageButton) findViewById(R.id.btnToggle);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否正在播放
                if (mediaPlayer.isPlaying()){
                    //暂停播放
                    pauseMediaplayer();
                }else if (isPrepared){
                    //开始播放
                    startMediaplayer();
                }else {
                    Toast.makeText(getContext(), "现在不能播放", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置进度条
        progressBar  = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(PROGRESS_MAX);
        //全屏播放按钮
        findViewById(R.id.btnFullScreen).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到全屏播放

            }
        });
    }
    //初始化MediaPlayer
    private void initMediaPlayer(){
        mediaPlayer = new MediaPlayer(getContext());
        mediaPlayer.setDisplay(surfaceHolder);
        //准备监听
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                startMediaplayer();
            }
        });
        //audio处理
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what==MediaPlayer.MEDIA_INFO_FILE_OPEN_OK){
                    mediaPlayer.audioInitedOk(mediaPlayer.audioTrackInit());
                    return true;
                }
                return false;
            }
        });
        //视频大小改变监听
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                //参数的寛高。是指视频的宽和高，我们可以通过参数去设置surfaceView的寛高
                int layoutWith = surfaceView.getWidth();
                int layoutHeight = layoutWith*height/width;
                //更新SurfaceView寛高
                ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
                params.width = layoutWith;
                params.height = layoutHeight;
                surfaceView.setLayoutParams(params);
            }
        });
    }
    //准备MediaPlayer
    private void  prepareMediaPlayer(){
        //重置MediaPlayer

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoPath);
            //设置循环播放
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
            imageView.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //开始播放
    private void startMediaplayer(){
        imageView.setVisibility(View.INVISIBLE);
        imageButton.setImageResource(R.drawable.ic_pause);
        mediaPlayer.start();
        isPlaying = true;
        //进度条操作
        handler.sendEmptyMessage(0);
    }
    //暂停播放
    private void pauseMediaplayer(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        isPlaying = false;
        imageButton.setImageResource(R.drawable.ic_play_arrow);
        //进度条操作
        handler.removeMessages(0);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //每0.2秒更新进度条
            if (isPlaying){
                int progerss = (int)(mediaPlayer.getCurrentPosition()*PROGRESS_MAX/mediaPlayer.getDuration());
                progressBar.setProgress(progerss);
                //发送一个空的延迟消息，不停调用本身，实现自动更新进度条
                handler.sendEmptyMessageDelayed(0,200);
            }
        }
    };
    //释放MediaPlayer
    private void releaseMediaPlayer(){
        mediaPlayer.release();
        mediaPlayer = null;
        isPrepared = false;
        progressBar.setProgress(0);
    }
}
