package org.kymjs.blog.ui.fragment;

import org.kymjs.blog.R;
import org.kymjs.blog.ui.widget.RecordButton;
import org.kymjs.blog.ui.widget.RecordButton.OnFinishedRecordListener;
import org.kymjs.blog.ui.widget.RecordButtonUtil.OnPlayListener;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.DensityUtils;
import org.kymjs.kjframe.utils.StringUtils;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 语音动弹发布界面
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public class TweetRecordFragment extends TitleBarFragment {

    @BindView(id = R.id.tweet_btn_record)
    RecordButton mBtnRecort;
    @BindView(id = R.id.tweet_time_record)
    TextView mTvTime;
    @BindView(id = R.id.tweet_text_record)
    TextView mTvInputLen;
    @BindView(id = R.id.tweet_edit_record)
    EditText mEtSpeech;
    @BindView(id = R.id.tweet_img_volume)
    ImageView mImgVolume;
    @BindView(id = R.id.tweet_img_add, click = true)
    ImageView mImgAdd;
    @BindView(id = R.id.tweet_layout_record, click = true)
    RelativeLayout mLayout;

    public static int MAX_LEN = 160;

    private AnimationDrawable drawable; // 录音播放时的动画背景

    // private final String strSpeech = "#语音动弹#";
    private String imageUri;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View rootView = inflater.inflate(R.layout.frag_tweet_pub_record,
                container, false);
        return rootView;
    }

    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        super.setActionBarRes(actionBarRes);
        actionBarRes.title = getString(R.string.str_tweet_pub_record);
        actionBarRes.backImageId = R.drawable.titlebar_back;
        actionBarRes.menuImageId = R.drawable.titlebar_add;
    }

    @Override
    public void onMenuClick() {
        super.onMenuClick();
        Intent intent = new Intent();
        intent.putExtra(TweetFragment.CONTENT_KEY, mEtSpeech.getText()
                .toString());
        if (!StringUtils.isEmpty(imageUri)) {
            intent.putExtra(TweetFragment.IMAGEPATH_KEY, imageUri);
            outsideAty.setResult(TweetFragment.REQUEST_CODE_IMAGE, intent);
        } else {
            intent.putExtra(TweetFragment.AUDIOPATH_KEY,
                    mBtnRecort.getCurrentAudioPath());
            outsideAty.setResult(TweetFragment.REQUEST_CODE_RECORD, intent);
        }
        outsideAty.finish();
    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        RelativeLayout.LayoutParams params = (LayoutParams) mBtnRecort
                .getLayoutParams();
        params.width = DensityUtils.getScreenW(getActivity());
        params.height = (int) (DensityUtils.getScreenH(getActivity()) * 0.4);
        mBtnRecort.setLayoutParams(params);

        mBtnRecort.setOnFinishedRecordListener(new OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int recordTime) {
                mLayout.setVisibility(View.VISIBLE);
                if (recordTime < 10) {
                    mTvTime.setText("0" + recordTime + "\"");
                } else {
                    mTvTime.setText(recordTime + "\"");
                }
                mImgAdd.setVisibility(View.GONE);
                imageUri = null;
            }

            @Override
            public void onCancleRecord() {
                mLayout.setVisibility(View.GONE);
            }
        });

        drawable = (AnimationDrawable) mImgVolume.getBackground();
        mBtnRecort.getAudioUtil().setOnPlayListener(new OnPlayListener() {
            @Override
            public void stopPlay() {
                drawable.stop();
                mImgVolume.setBackgroundDrawable(drawable.getFrame(0));
            }

            @Override
            public void starPlay() {
                mImgVolume.setBackgroundDrawable(drawable);
                drawable.start();
            }
        });

        mEtSpeech.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() > MAX_LEN) {
                    mTvInputLen.setText("已达到最大长度");
                } else {
                    mTvInputLen.setText("您还可以输入" + (MAX_LEN - s.length())
                            + "个字符");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_LEN) {
                    mEtSpeech.setText(s.subSequence(0, MAX_LEN));
                    CharSequence text = mEtSpeech.getText();
                    if (text instanceof Spannable) {
                        Selection.setSelection((Spannable) text, MAX_LEN);
                    }
                }
            }
        });
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.tweet_layout_record:
            mBtnRecort.playRecord();
            break;
        case R.id.tweet_img_add:
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");
            startActivityForResult(intent, 111);
            break;
        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            mImgAdd.setImageURI(data.getData());
            imageUri = data.getDataString();
        }
    }
}
