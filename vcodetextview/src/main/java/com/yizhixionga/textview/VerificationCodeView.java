package com.yizhixionga.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 方框验证码View,显示用LinearLayout里面放TextView,
 * 然后有一个EditText覆盖在上面，然后再监听输入，显示在TextView上
 */
public class VerificationCodeView extends RelativeLayout {

    OnVerificationCodeCompleteListener onVerificationCodeCompleteListener;

    //输入的长度
    private int vCodeLength = 6;
    //输入的内容
    private String inputData;
    private EditText editText;

    //TextView的list
    private List<TextView> tvList = new ArrayList<>();
    //输入框默认背景
    private int tvBgNormal = R.drawable.bg_normal;
    //输入框焦点背景
    private int tvBgFocus = R.drawable.bg_focus;
    //输入框的间距
    private int tvMarginRight = 10;
    //TextView宽
    private int tvWidth = 45;
    //TextView高
    private int tvHeight = 45;
    //TextView字体颜色
    private int tvTextColor;
    //TextView字体大小
    private float tvTextSize = 20;
    //字体是否加粗
    private boolean isBold = false;

    public VerificationCodeView(Context context) {
        this(context, null);
    }

    public VerificationCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

public VerificationCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    //获取自定义样式的属性
    TypedArray typedArray =context.obtainStyledAttributes(attrs,R.styleable.VCodeTextView);
    vCodeLength = typedArray.getInteger(R.styleable.VCodeTextView_vCodeDataLength,6);
    tvBgNormal = typedArray.getResourceId(R.styleable.VCodeTextView_vCodeBackgroundNormal,R.drawable.bg_normal);
    tvBgFocus = typedArray.getResourceId(R.styleable.VCodeTextView_vCodeBackgroundFocus,R.drawable.bg_focus);
    tvMarginRight = typedArray.getDimensionPixelSize(R.styleable.VCodeTextView_vCodeMargin, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics()));
    tvWidth = typedArray.getDimensionPixelSize(R.styleable.VCodeTextView_vCodeWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,45,getResources().getDisplayMetrics()));
    tvHeight = typedArray.getDimensionPixelSize(R.styleable.VCodeTextView_vCodeHeight, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,45,getResources().getDisplayMetrics()));
    tvTextSize = typedArray.getDimensionPixelSize(R.styleable.VCodeTextView_vCodeTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics()));
    tvTextColor = typedArray.getColor(R.styleable.VCodeTextView_vCodeTextColor, Color.BLACK);
    isBold = typedArray.getBoolean(R.styleable.VCodeTextView_vCodeTextBold,false);
    //用完回收
    typedArray.recycle();
    init();
}


private void init() {
    initTextView();
    initEditText();
    tvSetFocus(0);
}

/**
 * 输入框和父布局一样大，但字体大小0，看不见的
 */
private void initEditText() {
    editText = new EditText(getContext());
    addView(editText);
    LayoutParams layoutParams = (LayoutParams) editText.getLayoutParams();
    layoutParams.width = layoutParams.MATCH_PARENT;
    layoutParams.height = tvHeight;
    editText.setLayoutParams(layoutParams);

    //防止横盘小键盘全屏显示
    editText.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);
    //隐藏光标
    editText.setCursorVisible(false);
    //最大输入长度
    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(vCodeLength)});
    //输入类型为数字
    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    editText.setTextSize(0);
    editText.setBackgroundResource(0);

    editText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s != null && !TextUtils.isEmpty(s.toString())) {
                //有验证码的情况
                inputData = s.toString();

                //如果是最后一位验证码，焦点在最后一个，否者在下一位
                if (inputData.length() == vCodeLength) {
                    tvSetFocus(vCodeLength - 1);
                } else {
                    tvSetFocus(inputData.length());
                }

                //给textView设置数据
                for (int i = 0; i < inputData.length(); i++) {
                    tvList.get(i).setText(inputData.substring(i, i + 1));
                }
                for (int i = inputData.length(); i < vCodeLength; i++) {
                    tvList.get(i).setText("");
                }
            } else {
                //一位验证码都没有的情况
                tvSetFocus(0);
                for (int i = 0; i < vCodeLength; i++) {
                    tvList.get(i).setText("");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (onVerificationCodeCompleteListener != null) {
                if (s.length() == vCodeLength) {
                    onVerificationCodeCompleteListener.verificationCodeComplete(s.toString());
                } else {
                    onVerificationCodeCompleteListener.verificationCodeIncomplete(s.toString());
                }
            }
        }
    });
}

    /**
     * 设置TextView
     */
    private void initTextView() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        addView(linearLayout);
        LayoutParams llLayoutParams = (LayoutParams) linearLayout.getLayoutParams();
        llLayoutParams.width = LayoutParams.MATCH_PARENT;
        llLayoutParams.height = LayoutParams.WRAP_CONTENT;
        //linearLayout.setLayoutParams(llLayoutParams);
        //水平排列
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        //内容居中
        linearLayout.setGravity(Gravity.CENTER);
        for (int i = 0; i < vCodeLength; i++) {
            TextView textView = new TextView(getContext());
            linearLayout.addView(textView);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.width = tvWidth;
            layoutParams.height = tvHeight;
            //只需将中间隔开，所以最后一个textView不需要margin
            if (i == vCodeLength - 1) {
                layoutParams.rightMargin = 0;
            } else {
                layoutParams.rightMargin = tvMarginRight;
            }

            //textView.setLayoutParams(layoutParams);
            textView.setBackgroundResource(tvBgNormal);
            if (isBold){
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            textView.setGravity(Gravity.CENTER);
            //注意单位
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,tvTextSize);
            textView.setTextColor(tvTextColor);

            tvList.add(textView);
        }
    }

/**
 * 假装获取焦点
 */
private void tvSetFocus(int index) {
    tvSetFocus(tvList.get(index));
}

private void tvSetFocus(TextView textView) {
    for (int i = 0; i < vCodeLength; i++) {
        tvList.get(i).setBackgroundResource(tvBgNormal);
    }
    //重新获取焦点
    textView.setBackgroundResource(tvBgFocus);
}

    public int dp2px(final float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dp(final float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public int sp2px(final float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int px2sp(final float pxValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

/**
 * 输入完成回调接口
 */
public interface OnVerificationCodeCompleteListener {
    //完成输入
    void verificationCodeComplete(String verificationCode);

    //未完成输入
    void verificationCodeIncomplete(String verificationCode);
}

public void setOnVerificationCodeCompleteListener(OnVerificationCodeCompleteListener onVerificationCodeCompleteListener) {
    this.onVerificationCodeCompleteListener = onVerificationCodeCompleteListener;
}


    /**
     * 直接设置验证码
     */
    public void setInputData(String inputData) {
        this.inputData = inputData;
        editText.setText(inputData);
    }
}