package com.yizhixionga.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:YiZhiXiongA
 * time:2022/3/9 14:47
 **/
public class VCodeTextView extends RelativeLayout {
    //输入字数
    private int length = 6 ;
    //输入的数据
    private String data;
    private EditText editText;

    //框的list
    private List<TextView> list = new ArrayList<>();
    //输入框默认背景
    private int bgNormal = R.drawable.bg_normal;
    //输入框选中背景
    private int bgFocus = R.drawable.bg_focus;
    //间距
    private int marginRight = 10;
    //输入框宽度
    private int tvWidth = 45 ;
    //输入框高度
    private int tvHeight = 45 ;
    //字体颜色
    private int tvColor ;
    //字体大小
    private float tvSize = 8 ;

    private VCodeCompleteListener listener;

    public VCodeTextView(Context context) {
        this(context,null);
    }

    public VCodeTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VCodeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray =context.obtainStyledAttributes(attrs,R.styleable.VCodeTextView);
        length = typedArray.getInteger(R.styleable.VCodeTextView_vCodeDataLength,6);
        bgNormal = typedArray.getResourceId(R.styleable.VCodeTextView_vCodeBackgroundNormal,R.drawable.bg_normal);
        bgFocus = typedArray.getResourceId(R.styleable.VCodeTextView_vCodeBackgroundFocus,R.drawable.bg_focus);
        marginRight = typedArray.getDimensionPixelSize(R.styleable.VCodeTextView_vCodeMargin, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics()));
        tvWidth = typedArray.getDimensionPixelSize(R.styleable.VCodeTextView_vCodeWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,45,getResources().getDisplayMetrics()));
        tvHeight = typedArray.getDimensionPixelSize(R.styleable.VCodeTextView_vCodeHeight, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,45,getResources().getDisplayMetrics()));
        tvSize = typedArray.getDimensionPixelSize(R.styleable.VCodeTextView_vCodeTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,8,getResources().getDisplayMetrics()));
        tvColor = typedArray.getColor(R.styleable.VCodeTextView_vCodeTextColor, Color.BLACK);
        typedArray.recycle();
        init();
    }

    private void init(){
        initTextView();
        initEditText();
        tvSetFocus(0);
    }

    private void initTextView(){
        LinearLayout linearLayout =new LinearLayout(getContext());
        addView(linearLayout);
        LayoutParams llLayoutParams = (LayoutParams) linearLayout.getLayoutParams();
        llLayoutParams.width = LayoutParams.MATCH_PARENT;
        llLayoutParams.height = LayoutParams.WRAP_CONTENT;
        linearLayout.setLayoutParams(llLayoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        for (int i = 0; i < length; i++) {
            TextView textView =new TextView(getContext());
            linearLayout.addView(textView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.weight = tvWidth;
            layoutParams.height = tvHeight;
            //最后一个不设置marginRight
            if (i == length -1){
                layoutParams.rightMargin = 0;
            }else {
                layoutParams.rightMargin = marginRight;
            }
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(tvColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,tvSize);
            textView.setBackgroundResource(bgNormal);
            textView.setGravity(Gravity.CENTER);
            list.add(textView);
        }
    }

    private void initEditText(){
        //创建透明的EditText,调出键盘
        editText = new EditText(getContext());
        addView(editText);
        LayoutParams layoutParams = (LayoutParams) editText.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = tvHeight;
        editText.setLayoutParams(layoutParams);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);
        editText.setCursorVisible(false);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTextSize(0);
        editText.setBackgroundResource(0);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null && !TextUtils.isEmpty(charSequence.toString())){
                    data = charSequence.toString();
                    //如果是最后一位，则焦点在最后一位上，否则焦点在下一位身上
                    if (data.length() == length){
                        tvSetFocus(length - 1);
                    }else {
                        tvSetFocus(data.length());
                    }

                    //给textview设置数据
                    for (int i3 = 0; i3 < data.length(); i3++) {
                        list.get(i3).setText(data.substring(i3,i3+1));
                    }
                    for (int i3 = data.length(); i3 < length; i3++) {
                        list.get(i3).setText("");
                    }
                }else {
                    //输入框数据为空的时候
                    tvSetFocus(0);
                    for (int i3 = 0; i3 < length; i3++) {
                        list.get(i3).setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //完成回调
                if (null != listener){
                    if (editable.length() == length){
                        listener.VCodeComplete(editable.toString());
                    }
                }
            }
        });

    }
    private void tvSetFocus(int index){
        tvSetFocus(list.get(index));
    }

    private void tvSetFocus(TextView textView){
        for (int i = 0; i < length; i++) {
            list.get(i).setBackgroundResource(bgNormal);
        }
        textView.setBackgroundResource(bgFocus);
    }


    public interface VCodeCompleteListener{
        void VCodeComplete(String code);
    }

    //创建完成回调
    public void setVCodeCompleteListener(VCodeCompleteListener codeCompleteListener){
        listener=codeCompleteListener;
    }
}
