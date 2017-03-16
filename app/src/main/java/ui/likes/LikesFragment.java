package ui.likes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.yys.videonewss.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class LikesFragment extends Fragment {

    @BindView(R.id.tvUsername)
    TextView mTvUsername;
    @BindView(R.id.btnRegister)
    Button mBtnRegister;
    @BindView(R.id.btnLogin)
    Button mBtnLogin;
    @BindView(R.id.btnLogout)
    Button mBtnLogout;
    @BindView(R.id.divider)
    View mDivider;

    private View view;

    private LoginFragment loginfragment;
    private RegisterFragment registerfragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_likes,container,false);
            ButterKnife.bind(this,view);
            // TODO: 2017/3/15 0015 判断用户登录信息，如果已登录，则自动登录
        }
        return view;
    }

    @OnClick({R.id.btnRegister,R.id.btnLogin,R.id.btnLogout})
    public void onClick(View view){
        switch (view.getId()){
            //注册
            case R.id.btnRegister:
                if (registerfragment == null){
                    registerfragment = new RegisterFragment();
                }
                registerfragment.show(getChildFragmentManager(),"Register Dialog");
                break;
            //登录
            case R.id.btnLogin:
                if (loginfragment == null){
                    loginfragment = new LoginFragment();
                }
                loginfragment.show(getChildFragmentManager(),"Login Dialog");
                break;
            //退出登录
            case R.id.btnLogout:
                Toast.makeText(getContext(), "退出登录（待实现）", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
