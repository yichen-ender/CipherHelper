package com.hexihe.cipherhelper;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FunctionActivity extends Activity {

    private TextView tvTitle, tvResult;
    private EditText etInput, etParam1, etParam2, etParam3;
    private Button btnAction, btnCopy, btnBack, btnDecrypt;
    private ImageView ivQR;

    private int functionIndex = 0;
    private String functionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvResult = (TextView) findViewById(R.id.tv_result);
        etInput = (EditText) findViewById(R.id.et_input);
        etParam1 = (EditText) findViewById(R.id.et_param1);
        etParam2 = (EditText) findViewById(R.id.et_param2);
        etParam3 = (EditText) findViewById(R.id.et_param3);
        btnAction = (Button) findViewById(R.id.btn_action);
        btnCopy = (Button) findViewById(R.id.btn_copy);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnDecrypt = (Button) findViewById(R.id.btn_decrypt);
        ivQR = (ImageView) findViewById(R.id.iv_qr);

        Intent intent = getIntent();
        functionIndex = intent.getIntExtra("index", 0);
        functionName = intent.getStringExtra("name");
        tvTitle.setText(functionName);

        if (functionIndex == -1) {
            // 扫码结果展示
            String preset = intent.getStringExtra("preset");
            etInput.setVisibility(View.GONE);
            btnAction.setVisibility(View.GONE);
            btnDecrypt.setVisibility(View.GONE);
            btnCopy.setVisibility(View.VISIBLE);
            if (preset != null) {
                tvResult.setText(preset);
            } else {
                tvResult.setText("无扫描结果");
            }
        } else {
            setupFunctionUI();
        }

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAction(true);
            }
        });

        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAction(false);
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = tvResult.getText().toString();
                if (!result.isEmpty()) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("result", result);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(FunctionActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupFunctionUI() {
        etInput.setVisibility(View.VISIBLE);
        btnCopy.setVisibility(View.VISIBLE);
        ivQR.setVisibility(View.GONE);

        switch (functionIndex) {
            case 0: // 凯撒密码
                etParam1.setVisibility(View.VISIBLE);
                etParam1.setHint("偏移量 (默认3)");
                etParam1.setText("3");
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 1: // 栅栏密码
                etParam1.setVisibility(View.VISIBLE);
                etParam1.setHint("栏数 (默认2)");
                etParam1.setText("2");
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 2: // 倒序
                btnAction.setText("执行");
                btnDecrypt.setVisibility(View.GONE);
                break;
            case 3: // QWE密码
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 4: // 莫尔斯电码
                etParam1.setVisibility(View.VISIBLE);
                etParam1.setHint("滴 (默认.)");
                etParam1.setText(".");
                etParam2.setVisibility(View.VISIBLE);
                etParam2.setHint("嗒 (默认-)");
                etParam2.setText("-");
                etParam3.setVisibility(View.VISIBLE);
                etParam3.setHint("分隔符 (默认空格)");
                etParam3.setText(" ");
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 5: // 键盘V密码
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 6: // 埃特巴什码
                btnAction.setText("执行");
                btnDecrypt.setVisibility(View.GONE);
                break;
            case 7: // 键盘坐标
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 8: // 手机键盘
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 9: // 字母表
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 10: // 一个词暴力
                btnAction.setText("执行");
                btnDecrypt.setVisibility(View.GONE);
                break;
            case 11: // 维吉尼亚密码
                etParam1.setVisibility(View.VISIBLE);
                etParam1.setHint("密钥");
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 12: // 元素周期表
                btnAction.setText("加密");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解密");
                break;
            case 13: // 增删空格
                btnAction.setText("增加空格");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("删除空格");
                break;
            case 14: // 替换字符
                etParam1.setVisibility(View.VISIBLE);
                etParam1.setHint("原字符");
                etParam2.setVisibility(View.VISIBLE);
                etParam2.setHint("替换为");
                btnAction.setText("替换");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("还原");
                break;
            case 15: // 进制转换
                etParam1.setVisibility(View.VISIBLE);
                etParam1.setHint("原进制 (如10)");
                etParam1.setText("10");
                etParam2.setVisibility(View.VISIBLE);
                etParam2.setHint("目标进制 (如16)");
                etParam2.setText("16");
                btnAction.setText("执行");
                btnDecrypt.setVisibility(View.GONE);
                break;
            case 16: // MD5
                btnAction.setText("执行");
                btnDecrypt.setVisibility(View.GONE);
                break;
            case 17: // 大小写转换
                btnAction.setText("转大写");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("转小写");
                break;
            case 18: // 字母频率
                btnAction.setText("执行");
                btnDecrypt.setVisibility(View.GONE);
                break;
            case 19: // Base64
                btnAction.setText("编码");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解码");
                break;
            case 20: // Unicode
                btnAction.setText("编码");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解码");
                break;
            case 21: // 中文电码
                btnAction.setText("执行");
                btnDecrypt.setVisibility(View.GONE);
                break;
            case 22: // 区位码
                btnAction.setText("编码");
                btnDecrypt.setVisibility(View.VISIBLE);
                btnDecrypt.setText("解码");
                break;
            case 23: // 二维码
                btnAction.setText("生成");
                btnDecrypt.setVisibility(View.GONE);
                break;
            default:
                btnAction.setText("执行");
                btnDecrypt.setVisibility(View.GONE);
                break;
        }
    }

    private void performAction(boolean isEncrypt) {
        String input = etInput.getText().toString();
        String p1 = etParam1.getText().toString();
        String p2 = etParam2.getText().toString();
        String p3 = etParam3.getText().toString();
        String result = "";

        try {
            switch (functionIndex) {
                case 0: { // 凯撒密码
                    int shift = p1.isEmpty() ? 3 : Integer.parseInt(p1);
                    result = isEncrypt ? CipherEngine.caesarEncrypt(input, shift)
                                       : CipherEngine.caesarDecrypt(input, shift);
                    break;
                }
                case 1: { // 栅栏密码
                    int rails = p1.isEmpty() ? 2 : Integer.parseInt(p1);
                    result = isEncrypt ? CipherEngine.railFenceEncrypt(input, rails)
                                       : CipherEngine.railFenceDecrypt(input, rails);
                    break;
                }
                case 2: // 倒序
                    result = CipherEngine.reverse(input);
                    break;
                case 3: // QWE密码
                    result = isEncrypt ? CipherEngine.qweEncrypt(input)
                                       : CipherEngine.qweDecrypt(input);
                    break;
                case 4: { // 莫尔斯电码
                    String dot = p1.isEmpty() ? "." : p1;
                    String dash = p2.isEmpty() ? "-" : p2;
                    String sep = p3.isEmpty() ? " " : p3;
                    result = isEncrypt ? CipherEngine.morseEncrypt(input, dot, dash, sep)
                                       : CipherEngine.morseDecrypt(input, dot, dash, sep);
                    break;
                }
                case 5: // 键盘V密码
                    result = isEncrypt ? CipherEngine.keyboardVEncrypt(input)
                                       : CipherEngine.keyboardVDecrypt(input);
                    break;
                case 6: // 埃特巴什码
                    result = CipherEngine.atbash(input);
                    break;
                case 7: // 键盘坐标
                    result = isEncrypt ? CipherEngine.keyboardCoordEncrypt(input)
                                       : CipherEngine.keyboardCoordDecrypt(input);
                    break;
                case 8: // 手机键盘
                    result = isEncrypt ? CipherEngine.phoneKeyboardEncrypt(input)
                                       : CipherEngine.phoneKeyboardDecrypt(input);
                    break;
                case 9: // 字母表
                    result = isEncrypt ? CipherEngine.alphabetEncrypt(input)
                                       : CipherEngine.alphabetDecrypt(input);
                    break;
                case 10: // 一个词暴力
                    result = CipherEngine.bruteForceCaesar(input);
                    break;
                case 11: { // 维吉尼亚密码
                    String key = p1;
                    result = isEncrypt ? CipherEngine.vigenereEncrypt(input, key)
                                       : CipherEngine.vigenereDecrypt(input, key);
                    break;
                }
                case 12: // 元素周期表
                    result = isEncrypt ? CipherEngine.elementEncrypt(input)
                                       : CipherEngine.elementDecrypt(input);
                    break;
                case 13: // 增删空格
                    result = isEncrypt ? CipherEngine.addSpaces(input)
                                       : CipherEngine.removeSpaces(input);
                    break;
                case 14: { // 替换字符
                    String from = p1;
                    String to = p2;
                    result = isEncrypt ? CipherEngine.replaceChars(input, from, to)
                                       : CipherEngine.restoreChars(input, from, to);
                    break;
                }
                case 15: { // 进制转换
                    int fromBase = p1.isEmpty() ? 10 : Integer.parseInt(p1);
                    int toBase = p2.isEmpty() ? 16 : Integer.parseInt(p2);
                    result = CipherEngine.radixConvert(input, fromBase, toBase);
                    break;
                }
                case 16: // MD5
                    result = CipherEngine.md5(input);
                    break;
                case 17: // 大小写转换
                    result = CipherEngine.caseConvert(input, isEncrypt);
                    break;
                case 18: // 字母频率
                    result = CipherEngine.letterFrequency(input);
                    break;
                case 19: // Base64
                    result = isEncrypt ? CipherEngine.base64Encrypt(input)
                                       : CipherEngine.base64Decrypt(input);
                    break;
                case 20: // Unicode
                    result = isEncrypt ? CipherEngine.unicodeEncrypt(input)
                                       : CipherEngine.unicodeDecrypt(input);
                    break;
                case 21: // 中文电码
                    result = CipherEngine.chineseTelegraph(input);
                    break;
                case 22: // 区位码
                    result = isEncrypt ? CipherEngine.quWeiMaEncrypt(input)
                                       : CipherEngine.quWeiMaDecrypt(input);
                    break;
                case 23: { // 二维码
                    ivQR.setVisibility(View.VISIBLE);
                    Bitmap bmp = CipherEngine.generateQRCode(input, 600);
                    if (bmp != null) {
                        ivQR.setImageBitmap(bmp);
                        result = "二维码已生成";
                    } else {
                        result = "生成失败";
                    }
                    break;
                }
                default:
                    result = "未知功能";
                    break;
            }
        } catch (Exception e) {
            result = "错误: " + e.getMessage();
        }

        tvResult.setText(result);
    }
}
