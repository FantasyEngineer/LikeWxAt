package com.example.likewxat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.et);
        mEditText.setFilters(new InputFilter[]{new MyInputFilter()});
    }

    public void getET(View view) {
        Log.d("getET", "getET: " + mEditText.getText().toString());
    }

    /**
     * 识别输入框的是不是@符号
     */
    private class MyInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // TODO Auto-generated method stub
            if (source.toString().equalsIgnoreCase("@")
                    || source.toString().equalsIgnoreCase("＠")) {
                goAt();
            }

            return source;
        }
    }

    private void goAt() {
        StringBuffer tmp = new StringBuffer();
        // 把选中人的id已空格分隔，拼接成字符串
        for (Map.Entry<String, String> entry : cidNameMap.entrySet()) {
            tmp.append(entry.getKey() + " ");
        }

        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra(PersonActivity.KEY_SELECTED, tmp.toString());
        startActivityForResult(intent, CODE_PERSON);
    }

    private static final int CODE_PERSON = 1;
    /**
     * 存储@的cid、name对
     */
    private Map<String, String> cidNameMap = new HashMap<String, String>();

    /**
     * 选中的@的人的cid,进入@列表时，需要传递过去
     */
    private String selectedCids;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != RESULT_OK) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CODE_PERSON:

                String tmpCidStr = data.getStringExtra(PersonActivity.KEY_CID);
                String tmpNameStr = data.getStringExtra(PersonActivity.KEY_NAME);

                String[] tmpCids = tmpCidStr.split(" ");
                String[] tmpNames = tmpNameStr.split(" ");

                if (tmpCids != null && tmpCids.length > 0) {
                    for (int i = 0; i < tmpCids.length; i++) {
                        if (tmpNames.length > i) {
                            cidNameMap.put(tmpCids[i], tmpNames[i]);
                        }
                    }
                }

                if (selectedCids == null) {
                    selectedCids = tmpCidStr;
                } else {
                    selectedCids = selectedCids + tmpCidStr;
                }

                if (nameStr == null) {
                    nameStr = tmpNameStr;
                } else {
                    nameStr = nameStr + tmpNameStr;
                }
                lastNameStr = tmpNameStr;

                // 获取光标当前位置
                int curIndex = mEditText.getSelectionStart();

                // 把要@的人插入光标所在位置
                mEditText.getText().insert(curIndex, lastNameStr);
                // 通过输入@符号进入好友列表并返回@的人，要删除之前输入的@
                if (curIndex >= 1) {
                    mEditText.getText().replace(curIndex - 1, curIndex, "");
                }
                setAtImageSpan(nameStr);

                break;

        }
    }

    /**
     * 返回的所有的用户名,用于识别输入框中的所有要@的人
     * <p>
     * 如果用户删除过，会出现不匹配的情况，需要在for循环中做处理
     */
    private String nameStr;

    /**
     * 上一次返回的用户名，用于把要@的用户名拼接到输入框中
     */
    private String lastNameStr;

    private void setAtImageSpan(String nameStr) {

        String content = String.valueOf(mEditText.getText());
        if (content.endsWith("@") || content.endsWith("＠")) {
            content = content.substring(0, content.length() - 1);
        }
        String tmp = content;

        SpannableString ss = new SpannableString(tmp);

        if (nameStr != null) {
            String[] names = nameStr.split(" ");
            if (names != null && names.length > 0) {
                for (String name : names) {
                    if (name != null && name.trim().length() > 0) {
                        final Bitmap bmp = getNameBitmap(name);

                        // 这里会出现删除过的用户，需要做判断，过滤掉
                        if (tmp.indexOf(name) >= 0
                                && (tmp.indexOf(name) + name.length()) <= tmp
                                .length()) {

                            // 把取到的要@的人名，用DynamicDrawableSpan代替
                            ss.setSpan(
                                    new DynamicDrawableSpan(
                                            DynamicDrawableSpan.ALIGN_BASELINE) {

                                        @Override
                                        public Drawable getDrawable() {
                                            // TODO Auto-generated method stub
                                            BitmapDrawable drawable = new BitmapDrawable(
                                                    getResources(), bmp);
                                            drawable.setBounds(0, 0,
                                                    bmp.getWidth(),
                                                    bmp.getHeight());
                                            return drawable;
                                        }
                                    }, tmp.indexOf(name),
                                    tmp.indexOf(name) + name.length(),
                                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }
        }
        mEditText.setTextKeepState(ss);
    }

    /**
     * 把返回的人名，转换成bitmap
     *
     * @param name
     * @return
     */
    private Bitmap getNameBitmap(String name) {

		/* 把@相关的字符串转换成bitmap 然后使用DynamicDrawableSpan加入输入框中 */

        name = "" + name;
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.color_blue));
        paint.setAntiAlias(true);
        paint.setTextSize(30);
        Rect rect = new Rect();

        paint.getTextBounds(name, 0, name.length(), rect);

        // 获取字符串在屏幕上的长度
        int width = (int) (paint.measureText(name));

        final Bitmap bmp = Bitmap.createBitmap(width, rect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        canvas.drawText(name, rect.left, rect.height() - rect.bottom, paint);

        return bmp;
    }
}
