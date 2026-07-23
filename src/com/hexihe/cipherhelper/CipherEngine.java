package com.hexihe.cipherhelper;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class CipherEngine {

    private static final String MORSE_CODE[] = {
        ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---",
        "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-",
        "..-", "...-", ".--", "-..-", "-.--", "--..", "-----", ".----", "..---",
        "...--", "....-", ".....", "-....", "--...", "---..", "----."
    };

    private static final String QWERTY = "QWERTYUIOPASDFGHJKLZXCVBNM";

    private static final String[] KEYBOARD_V_COLS = {
        "QAZ", "WSX", "EDC", "RFV", "TGB", "YHN", "UJM", "IK,", "OL.", "P;/"
    };

    private static final String[] KEYBOARD_ROWS = {
        "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"
    };

    private static final String PHONE_KEYS = "22233344455566677778889999";

    private static final String[] ELEMENTS = {
        "H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne",
        "Na", "Mg", "Al", "Si", "P", "S", "Cl", "Ar", "K", "Ca",
        "Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn",
        "Ga", "Ge", "As", "Se", "Br", "Kr", "Rb", "Sr", "Y", "Zr",
        "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "In", "Sn",
        "Sb", "Te", "I", "Xe", "Cs", "Ba", "La", "Ce", "Pr", "Nd",
        "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb",
        "Lu", "Hf", "Ta", "W", "Re", "Os", "Ir", "Pt", "Au", "Hg",
        "Tl", "Pb", "Bi", "Po", "At", "Rn", "Fr", "Ra", "Ac", "Th",
        "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm",
        "Md", "No", "Lr", "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds",
        "Rg", "Cn", "Nh", "Fl", "Mc", "Lv", "Ts", "Og"
    };

    private static final Map<String, String> TELEGRAPH_MAP = new HashMap<>();
    private static final Map<String, String> CODE_TO_CHAR = new HashMap<>();

    static {
        TELEGRAPH_MAP.put("一", "0001");
        CODE_TO_CHAR.put("0001", "一");
        TELEGRAPH_MAP.put("乙", "0002");
        CODE_TO_CHAR.put("0002", "乙");
        TELEGRAPH_MAP.put("二", "0003");
        CODE_TO_CHAR.put("0003", "二");
        TELEGRAPH_MAP.put("十", "0004");
        CODE_TO_CHAR.put("0004", "十");
        TELEGRAPH_MAP.put("丁", "0005");
        CODE_TO_CHAR.put("0005", "丁");
        TELEGRAPH_MAP.put("厂", "0006");
        CODE_TO_CHAR.put("0006", "厂");
        TELEGRAPH_MAP.put("七", "0007");
        CODE_TO_CHAR.put("0007", "七");
        TELEGRAPH_MAP.put("卜", "0008");
        CODE_TO_CHAR.put("0008", "卜");
        TELEGRAPH_MAP.put("八", "0009");
        CODE_TO_CHAR.put("0009", "八");
        TELEGRAPH_MAP.put("人", "0010");
        CODE_TO_CHAR.put("0010", "人");
        TELEGRAPH_MAP.put("入", "0011");
        CODE_TO_CHAR.put("0011", "入");
        TELEGRAPH_MAP.put("儿", "0012");
        CODE_TO_CHAR.put("0012", "儿");
        TELEGRAPH_MAP.put("几", "0013");
        CODE_TO_CHAR.put("0013", "几");
        TELEGRAPH_MAP.put("九", "0014");
        CODE_TO_CHAR.put("0014", "九");
        TELEGRAPH_MAP.put("了", "0015");
        CODE_TO_CHAR.put("0015", "了");
        TELEGRAPH_MAP.put("刀", "0016");
        CODE_TO_CHAR.put("0016", "刀");
        TELEGRAPH_MAP.put("力", "0017");
        CODE_TO_CHAR.put("0017", "力");
        TELEGRAPH_MAP.put("乃", "0018");
        CODE_TO_CHAR.put("0018", "乃");
        TELEGRAPH_MAP.put("又", "0019");
        CODE_TO_CHAR.put("0019", "又");
        TELEGRAPH_MAP.put("三", "0020");
        CODE_TO_CHAR.put("0020", "三");
        TELEGRAPH_MAP.put("干", "0021");
        CODE_TO_CHAR.put("0021", "干");
        TELEGRAPH_MAP.put("于", "0022");
        CODE_TO_CHAR.put("0022", "于");
        TELEGRAPH_MAP.put("工", "0023");
        CODE_TO_CHAR.put("0023", "工");
        TELEGRAPH_MAP.put("土", "0024");
        CODE_TO_CHAR.put("0024", "土");
        TELEGRAPH_MAP.put("士", "0025");
        CODE_TO_CHAR.put("0025", "士");
        TELEGRAPH_MAP.put("才", "0026");
        CODE_TO_CHAR.put("0026", "才");
        TELEGRAPH_MAP.put("下", "0027");
        CODE_TO_CHAR.put("0027", "下");
        TELEGRAPH_MAP.put("寸", "0028");
        CODE_TO_CHAR.put("0028", "寸");
        TELEGRAPH_MAP.put("大", "0029");
        CODE_TO_CHAR.put("0029", "大");
        TELEGRAPH_MAP.put("丈", "0030");
        CODE_TO_CHAR.put("0030", "丈");
        TELEGRAPH_MAP.put("与", "0031");
        CODE_TO_CHAR.put("0031", "与");
        TELEGRAPH_MAP.put("万", "0032");
        CODE_TO_CHAR.put("0032", "万");
        TELEGRAPH_MAP.put("上", "0033");
        CODE_TO_CHAR.put("0033", "上");
        TELEGRAPH_MAP.put("小", "0034");
        CODE_TO_CHAR.put("0034", "小");
        TELEGRAPH_MAP.put("口", "0035");
        CODE_TO_CHAR.put("0035", "口");
        TELEGRAPH_MAP.put("巾", "0036");
        CODE_TO_CHAR.put("0036", "巾");
        TELEGRAPH_MAP.put("山", "0037");
        CODE_TO_CHAR.put("0037", "山");
        TELEGRAPH_MAP.put("千", "0038");
        CODE_TO_CHAR.put("0038", "千");
        TELEGRAPH_MAP.put("乞", "0039");
        CODE_TO_CHAR.put("0039", "乞");
        TELEGRAPH_MAP.put("川", "0040");
        CODE_TO_CHAR.put("0040", "川");
        TELEGRAPH_MAP.put("亿", "0041");
        CODE_TO_CHAR.put("0041", "亿");
        TELEGRAPH_MAP.put("个", "0042");
        CODE_TO_CHAR.put("0042", "个");
        TELEGRAPH_MAP.put("夕", "0043");
        CODE_TO_CHAR.put("0043", "夕");
        TELEGRAPH_MAP.put("久", "0044");
        CODE_TO_CHAR.put("0044", "久");
        TELEGRAPH_MAP.put("凡", "0045");
        CODE_TO_CHAR.put("0045", "凡");
        TELEGRAPH_MAP.put("勺", "0046");
        CODE_TO_CHAR.put("0046", "勺");
        TELEGRAPH_MAP.put("及", "0047");
        CODE_TO_CHAR.put("0047", "及");
        TELEGRAPH_MAP.put("广", "0048");
        CODE_TO_CHAR.put("0048", "广");
        TELEGRAPH_MAP.put("亡", "0049");
        CODE_TO_CHAR.put("0049", "亡");
        TELEGRAPH_MAP.put("门", "0050");
        CODE_TO_CHAR.put("0050", "门");
        TELEGRAPH_MAP.put("丫", "0051");
        CODE_TO_CHAR.put("0051", "丫");
        TELEGRAPH_MAP.put("之", "0052");
        CODE_TO_CHAR.put("0052", "之");
        TELEGRAPH_MAP.put("尸", "0053");
        CODE_TO_CHAR.put("0053", "尸");
        TELEGRAPH_MAP.put("己", "0054");
        CODE_TO_CHAR.put("0054", "己");
        TELEGRAPH_MAP.put("已", "0055");
        CODE_TO_CHAR.put("0055", "已");
        TELEGRAPH_MAP.put("巳", "0056");
        CODE_TO_CHAR.put("0056", "巳");
        TELEGRAPH_MAP.put("弓", "0057");
        CODE_TO_CHAR.put("0057", "弓");
        TELEGRAPH_MAP.put("子", "0058");
        CODE_TO_CHAR.put("0058", "子");
        TELEGRAPH_MAP.put("卫", "0059");
        CODE_TO_CHAR.put("0059", "卫");
        TELEGRAPH_MAP.put("也", "0060");
        CODE_TO_CHAR.put("0060", "也");
        TELEGRAPH_MAP.put("女", "0061");
        CODE_TO_CHAR.put("0061", "女");
        TELEGRAPH_MAP.put("飞", "0062");
        CODE_TO_CHAR.put("0062", "飞");
        TELEGRAPH_MAP.put("刃", "0063");
        CODE_TO_CHAR.put("0063", "刃");
        TELEGRAPH_MAP.put("习", "0064");
        CODE_TO_CHAR.put("0064", "习");
        TELEGRAPH_MAP.put("叉", "0065");
        CODE_TO_CHAR.put("0065", "叉");
        TELEGRAPH_MAP.put("马", "0066");
        CODE_TO_CHAR.put("0066", "马");
        TELEGRAPH_MAP.put("乡", "0067");
        CODE_TO_CHAR.put("0067", "乡");
        TELEGRAPH_MAP.put("丰", "0068");
        CODE_TO_CHAR.put("0068", "丰");
        TELEGRAPH_MAP.put("王", "0069");
        CODE_TO_CHAR.put("0069", "王");
        TELEGRAPH_MAP.put("井", "0070");
        CODE_TO_CHAR.put("0070", "井");
        TELEGRAPH_MAP.put("开", "0071");
        CODE_TO_CHAR.put("0071", "开");
        TELEGRAPH_MAP.put("夫", "0072");
        CODE_TO_CHAR.put("0072", "夫");
        TELEGRAPH_MAP.put("天", "0073");
        CODE_TO_CHAR.put("0073", "天");
        TELEGRAPH_MAP.put("元", "0074");
        CODE_TO_CHAR.put("0074", "元");
        TELEGRAPH_MAP.put("无", "0075");
        CODE_TO_CHAR.put("0075", "无");
        TELEGRAPH_MAP.put("云", "0076");
        CODE_TO_CHAR.put("0076", "云");
        TELEGRAPH_MAP.put("专", "0077");
        CODE_TO_CHAR.put("0077", "专");
        TELEGRAPH_MAP.put("丐", "0078");
        CODE_TO_CHAR.put("0078", "丐");
        TELEGRAPH_MAP.put("扎", "0079");
        CODE_TO_CHAR.put("0079", "扎");
        TELEGRAPH_MAP.put("艺", "0080");
        CODE_TO_CHAR.put("0080", "艺");
        TELEGRAPH_MAP.put("木", "0081");
        CODE_TO_CHAR.put("0081", "木");
        TELEGRAPH_MAP.put("五", "0082");
        CODE_TO_CHAR.put("0082", "五");
        TELEGRAPH_MAP.put("支", "0083");
        CODE_TO_CHAR.put("0083", "支");
        TELEGRAPH_MAP.put("厅", "0084");
        CODE_TO_CHAR.put("0084", "厅");
        TELEGRAPH_MAP.put("不", "0085");
        CODE_TO_CHAR.put("0085", "不");
        TELEGRAPH_MAP.put("犬", "0086");
        CODE_TO_CHAR.put("0086", "犬");
        TELEGRAPH_MAP.put("太", "0087");
        CODE_TO_CHAR.put("0087", "太");
        TELEGRAPH_MAP.put("区", "0088");
        CODE_TO_CHAR.put("0088", "区");
        TELEGRAPH_MAP.put("历", "0089");
        CODE_TO_CHAR.put("0089", "历");
        TELEGRAPH_MAP.put("友", "0090");
        CODE_TO_CHAR.put("0090", "友");
        TELEGRAPH_MAP.put("匹", "0091");
        CODE_TO_CHAR.put("0091", "匹");
        TELEGRAPH_MAP.put("尤", "0092");
        CODE_TO_CHAR.put("0092", "尤");
        TELEGRAPH_MAP.put("车", "0093");
        CODE_TO_CHAR.put("0093", "车");
        TELEGRAPH_MAP.put("巨", "0094");
        CODE_TO_CHAR.put("0094", "巨");
        TELEGRAPH_MAP.put("牙", "0095");
        CODE_TO_CHAR.put("0095", "牙");
        TELEGRAPH_MAP.put("屯", "0096");
        CODE_TO_CHAR.put("0096", "屯");
        TELEGRAPH_MAP.put("戈", "0097");
        CODE_TO_CHAR.put("0097", "戈");
        TELEGRAPH_MAP.put("比", "0098");
        CODE_TO_CHAR.put("0098", "比");
        TELEGRAPH_MAP.put("互", "0099");
        CODE_TO_CHAR.put("0099", "互");
        TELEGRAPH_MAP.put("切", "0100");
        CODE_TO_CHAR.put("0100", "切");
        TELEGRAPH_MAP.put("瓦", "0101");
        CODE_TO_CHAR.put("0101", "瓦");
        TELEGRAPH_MAP.put("止", "0102");
        CODE_TO_CHAR.put("0102", "止");
        TELEGRAPH_MAP.put("少", "0103");
        CODE_TO_CHAR.put("0103", "少");
        TELEGRAPH_MAP.put("日", "0104");
        CODE_TO_CHAR.put("0104", "日");
        TELEGRAPH_MAP.put("中", "0105");
        CODE_TO_CHAR.put("0105", "中");
        TELEGRAPH_MAP.put("贝", "0106");
        CODE_TO_CHAR.put("0106", "贝");
        TELEGRAPH_MAP.put("冈", "0107");
        CODE_TO_CHAR.put("0107", "冈");
        TELEGRAPH_MAP.put("内", "0108");
        CODE_TO_CHAR.put("0108", "内");
        TELEGRAPH_MAP.put("水", "0109");
        CODE_TO_CHAR.put("0109", "水");
        TELEGRAPH_MAP.put("见", "0110");
        CODE_TO_CHAR.put("0110", "见");
        TELEGRAPH_MAP.put("午", "0111");
        CODE_TO_CHAR.put("0111", "午");
        TELEGRAPH_MAP.put("牛", "0112");
        CODE_TO_CHAR.put("0112", "牛");
        TELEGRAPH_MAP.put("手", "0113");
        CODE_TO_CHAR.put("0113", "手");
        TELEGRAPH_MAP.put("气", "0114");
        CODE_TO_CHAR.put("0114", "气");
        TELEGRAPH_MAP.put("毛", "0115");
        CODE_TO_CHAR.put("0115", "毛");
        TELEGRAPH_MAP.put("壬", "0116");
        CODE_TO_CHAR.put("0116", "壬");
        TELEGRAPH_MAP.put("升", "0117");
        CODE_TO_CHAR.put("0117", "升");
        TELEGRAPH_MAP.put("夭", "0118");
        CODE_TO_CHAR.put("0118", "夭");
        TELEGRAPH_MAP.put("长", "0119");
        CODE_TO_CHAR.put("0119", "长");
        TELEGRAPH_MAP.put("仁", "0120");
        CODE_TO_CHAR.put("0120", "仁");
        TELEGRAPH_MAP.put("什", "0121");
        CODE_TO_CHAR.put("0121", "什");
        TELEGRAPH_MAP.put("片", "0122");
        CODE_TO_CHAR.put("0122", "片");
        TELEGRAPH_MAP.put("仆", "0123");
        CODE_TO_CHAR.put("0123", "仆");
        TELEGRAPH_MAP.put("化", "0124");
        CODE_TO_CHAR.put("0124", "化");
        TELEGRAPH_MAP.put("仇", "0125");
        CODE_TO_CHAR.put("0125", "仇");
        TELEGRAPH_MAP.put("币", "0126");
        CODE_TO_CHAR.put("0126", "币");
        TELEGRAPH_MAP.put("仍", "0127");
        CODE_TO_CHAR.put("0127", "仍");
        TELEGRAPH_MAP.put("仅", "0128");
        CODE_TO_CHAR.put("0128", "仅");
        TELEGRAPH_MAP.put("斤", "0129");
        CODE_TO_CHAR.put("0129", "斤");
        TELEGRAPH_MAP.put("爪", "0130");
        CODE_TO_CHAR.put("0130", "爪");
        TELEGRAPH_MAP.put("反", "0131");
        CODE_TO_CHAR.put("0131", "反");
        TELEGRAPH_MAP.put("介", "0132");
        CODE_TO_CHAR.put("0132", "介");
        TELEGRAPH_MAP.put("父", "0133");
        CODE_TO_CHAR.put("0133", "父");
        TELEGRAPH_MAP.put("从", "0134");
        CODE_TO_CHAR.put("0134", "从");
        TELEGRAPH_MAP.put("仑", "0135");
        CODE_TO_CHAR.put("0135", "仑");
        TELEGRAPH_MAP.put("今", "0136");
        CODE_TO_CHAR.put("0136", "今");
        TELEGRAPH_MAP.put("凶", "0137");
        CODE_TO_CHAR.put("0137", "凶");
        TELEGRAPH_MAP.put("分", "0138");
        CODE_TO_CHAR.put("0138", "分");
        TELEGRAPH_MAP.put("乏", "0139");
        CODE_TO_CHAR.put("0139", "乏");
        TELEGRAPH_MAP.put("公", "0140");
        CODE_TO_CHAR.put("0140", "公");
        TELEGRAPH_MAP.put("仓", "0141");
        CODE_TO_CHAR.put("0141", "仓");
        TELEGRAPH_MAP.put("月", "0142");
        CODE_TO_CHAR.put("0142", "月");
        TELEGRAPH_MAP.put("氏", "0143");
        CODE_TO_CHAR.put("0143", "氏");
        TELEGRAPH_MAP.put("勿", "0144");
        CODE_TO_CHAR.put("0144", "勿");
        TELEGRAPH_MAP.put("欠", "0145");
        CODE_TO_CHAR.put("0145", "欠");
        TELEGRAPH_MAP.put("风", "0146");
        CODE_TO_CHAR.put("0146", "风");
        TELEGRAPH_MAP.put("丹", "0147");
        CODE_TO_CHAR.put("0147", "丹");
        TELEGRAPH_MAP.put("匀", "0148");
        CODE_TO_CHAR.put("0148", "匀");
        TELEGRAPH_MAP.put("乌", "0149");
        CODE_TO_CHAR.put("0149", "乌");
        TELEGRAPH_MAP.put("勾", "0150");
        CODE_TO_CHAR.put("0150", "勾");
        TELEGRAPH_MAP.put("凤", "0151");
        CODE_TO_CHAR.put("0151", "凤");
        TELEGRAPH_MAP.put("六", "0152");
        CODE_TO_CHAR.put("0152", "六");
        TELEGRAPH_MAP.put("文", "0153");
        CODE_TO_CHAR.put("0153", "文");
        TELEGRAPH_MAP.put("亢", "0154");
        CODE_TO_CHAR.put("0154", "亢");
        TELEGRAPH_MAP.put("火", "0155");
        CODE_TO_CHAR.put("0155", "火");
        TELEGRAPH_MAP.put("方", "0156");
        CODE_TO_CHAR.put("0156", "方");
        TELEGRAPH_MAP.put("斗", "0157");
        CODE_TO_CHAR.put("0157", "斗");
        TELEGRAPH_MAP.put("为", "0158");
        CODE_TO_CHAR.put("0158", "为");
        TELEGRAPH_MAP.put("忆", "0159");
        CODE_TO_CHAR.put("0159", "忆");
        TELEGRAPH_MAP.put("订", "0160");
        CODE_TO_CHAR.put("0160", "订");
        TELEGRAPH_MAP.put("计", "0161");
        CODE_TO_CHAR.put("0161", "计");
        TELEGRAPH_MAP.put("户", "0162");
        CODE_TO_CHAR.put("0162", "户");
        TELEGRAPH_MAP.put("认", "0163");
        CODE_TO_CHAR.put("0163", "认");
        TELEGRAPH_MAP.put("冗", "0164");
        CODE_TO_CHAR.put("0164", "冗");
        TELEGRAPH_MAP.put("心", "0165");
        CODE_TO_CHAR.put("0165", "心");
        TELEGRAPH_MAP.put("讥", "0166");
        CODE_TO_CHAR.put("0166", "讥");
        TELEGRAPH_MAP.put("尺", "0167");
        CODE_TO_CHAR.put("0167", "尺");
        TELEGRAPH_MAP.put("引", "0168");
        CODE_TO_CHAR.put("0168", "引");
        TELEGRAPH_MAP.put("丑", "0169");
        CODE_TO_CHAR.put("0169", "丑");
        TELEGRAPH_MAP.put("巴", "0170");
        CODE_TO_CHAR.put("0170", "巴");
        TELEGRAPH_MAP.put("孔", "0171");
        CODE_TO_CHAR.put("0171", "孔");
        TELEGRAPH_MAP.put("队", "0172");
        CODE_TO_CHAR.put("0172", "队");
        TELEGRAPH_MAP.put("办", "0173");
        CODE_TO_CHAR.put("0173", "办");
        TELEGRAPH_MAP.put("以", "0174");
        CODE_TO_CHAR.put("0174", "以");
        TELEGRAPH_MAP.put("允", "0175");
        CODE_TO_CHAR.put("0175", "允");
        TELEGRAPH_MAP.put("予", "0176");
        CODE_TO_CHAR.put("0176", "予");
        TELEGRAPH_MAP.put("邓", "0177");
        CODE_TO_CHAR.put("0177", "邓");
        TELEGRAPH_MAP.put("劝", "0178");
        CODE_TO_CHAR.put("0178", "劝");
        TELEGRAPH_MAP.put("双", "0179");
        CODE_TO_CHAR.put("0179", "双");
        TELEGRAPH_MAP.put("书", "0180");
        CODE_TO_CHAR.put("0180", "书");
        TELEGRAPH_MAP.put("幻", "0181");
        CODE_TO_CHAR.put("0181", "幻");
        TELEGRAPH_MAP.put("玉", "0182");
        CODE_TO_CHAR.put("0182", "玉");
        TELEGRAPH_MAP.put("刊", "0183");
        CODE_TO_CHAR.put("0183", "刊");
        TELEGRAPH_MAP.put("示", "0184");
        CODE_TO_CHAR.put("0184", "示");
        TELEGRAPH_MAP.put("末", "0185");
        CODE_TO_CHAR.put("0185", "末");
        TELEGRAPH_MAP.put("未", "0186");
        CODE_TO_CHAR.put("0186", "未");
        TELEGRAPH_MAP.put("击", "0187");
        CODE_TO_CHAR.put("0187", "击");
        TELEGRAPH_MAP.put("打", "0188");
        CODE_TO_CHAR.put("0188", "打");
        TELEGRAPH_MAP.put("巧", "0189");
        CODE_TO_CHAR.put("0189", "巧");
        TELEGRAPH_MAP.put("正", "0190");
        CODE_TO_CHAR.put("0190", "正");
        TELEGRAPH_MAP.put("扑", "0191");
        CODE_TO_CHAR.put("0191", "扑");
        TELEGRAPH_MAP.put("扒", "0192");
        CODE_TO_CHAR.put("0192", "扒");
        TELEGRAPH_MAP.put("功", "0193");
        CODE_TO_CHAR.put("0193", "功");
        TELEGRAPH_MAP.put("扔", "0194");
        CODE_TO_CHAR.put("0194", "扔");
        TELEGRAPH_MAP.put("去", "0195");
        CODE_TO_CHAR.put("0195", "去");
        TELEGRAPH_MAP.put("甘", "0196");
        CODE_TO_CHAR.put("0196", "甘");
        TELEGRAPH_MAP.put("世", "0197");
        CODE_TO_CHAR.put("0197", "世");
        TELEGRAPH_MAP.put("古", "0198");
        CODE_TO_CHAR.put("0198", "古");
        TELEGRAPH_MAP.put("本", "0199");
        CODE_TO_CHAR.put("0199", "本");
        TELEGRAPH_MAP.put("术", "0200");
        CODE_TO_CHAR.put("0200", "术");
        TELEGRAPH_MAP.put("可", "0201");
        CODE_TO_CHAR.put("0201", "可");
        TELEGRAPH_MAP.put("丙", "0202");
        CODE_TO_CHAR.put("0202", "丙");
        TELEGRAPH_MAP.put("左", "0203");
        CODE_TO_CHAR.put("0203", "左");
        TELEGRAPH_MAP.put("厉", "0204");
        CODE_TO_CHAR.put("0204", "厉");
        TELEGRAPH_MAP.put("石", "0205");
        CODE_TO_CHAR.put("0205", "石");
        TELEGRAPH_MAP.put("右", "0206");
        CODE_TO_CHAR.put("0206", "右");
        TELEGRAPH_MAP.put("布", "0207");
        CODE_TO_CHAR.put("0207", "布");
        TELEGRAPH_MAP.put("龙", "0208");
        CODE_TO_CHAR.put("0208", "龙");
        TELEGRAPH_MAP.put("平", "0209");
        CODE_TO_CHAR.put("0209", "平");
        TELEGRAPH_MAP.put("灭", "0210");
        CODE_TO_CHAR.put("0210", "灭");
        TELEGRAPH_MAP.put("轧", "0211");
        CODE_TO_CHAR.put("0211", "轧");
        TELEGRAPH_MAP.put("东", "0212");
        CODE_TO_CHAR.put("0212", "东");
        TELEGRAPH_MAP.put("卡", "0213");
        CODE_TO_CHAR.put("0213", "卡");
        TELEGRAPH_MAP.put("北", "0214");
        CODE_TO_CHAR.put("0214", "北");
        TELEGRAPH_MAP.put("占", "0215");
        CODE_TO_CHAR.put("0215", "占");
        TELEGRAPH_MAP.put("业", "0216");
        CODE_TO_CHAR.put("0216", "业");
        TELEGRAPH_MAP.put("旧", "0217");
        CODE_TO_CHAR.put("0217", "旧");
        TELEGRAPH_MAP.put("帅", "0218");
        CODE_TO_CHAR.put("0218", "帅");
        TELEGRAPH_MAP.put("归", "0219");
        CODE_TO_CHAR.put("0219", "归");
        TELEGRAPH_MAP.put("旦", "0220");
        CODE_TO_CHAR.put("0220", "旦");
        TELEGRAPH_MAP.put("目", "0221");
        CODE_TO_CHAR.put("0221", "目");
        TELEGRAPH_MAP.put("且", "0222");
        CODE_TO_CHAR.put("0222", "且");
        TELEGRAPH_MAP.put("叶", "0223");
        CODE_TO_CHAR.put("0223", "叶");
        TELEGRAPH_MAP.put("甲", "0224");
        CODE_TO_CHAR.put("0224", "甲");
        TELEGRAPH_MAP.put("申", "0225");
        CODE_TO_CHAR.put("0225", "申");
        TELEGRAPH_MAP.put("叮", "0226");
        CODE_TO_CHAR.put("0226", "叮");
        TELEGRAPH_MAP.put("电", "0227");
        CODE_TO_CHAR.put("0227", "电");
        TELEGRAPH_MAP.put("号", "0228");
        CODE_TO_CHAR.put("0228", "号");
        TELEGRAPH_MAP.put("田", "0229");
        CODE_TO_CHAR.put("0229", "田");
        TELEGRAPH_MAP.put("由", "0230");
        CODE_TO_CHAR.put("0230", "由");
        TELEGRAPH_MAP.put("只", "0231");
        CODE_TO_CHAR.put("0231", "只");
        TELEGRAPH_MAP.put("叭", "0232");
        CODE_TO_CHAR.put("0232", "叭");
        TELEGRAPH_MAP.put("史", "0233");
        CODE_TO_CHAR.put("0233", "史");
        TELEGRAPH_MAP.put("央", "0234");
        CODE_TO_CHAR.put("0234", "央");
        TELEGRAPH_MAP.put("兄", "0235");
        CODE_TO_CHAR.put("0235", "兄");
        TELEGRAPH_MAP.put("叼", "0236");
        CODE_TO_CHAR.put("0236", "叼");
        TELEGRAPH_MAP.put("叫", "0237");
        CODE_TO_CHAR.put("0237", "叫");
        TELEGRAPH_MAP.put("另", "0238");
        CODE_TO_CHAR.put("0238", "另");
        TELEGRAPH_MAP.put("叨", "0239");
        CODE_TO_CHAR.put("0239", "叨");
        TELEGRAPH_MAP.put("四", "0240");
        CODE_TO_CHAR.put("0240", "四");
        TELEGRAPH_MAP.put("生", "0241");
        CODE_TO_CHAR.put("0241", "生");
        TELEGRAPH_MAP.put("失", "0242");
        CODE_TO_CHAR.put("0242", "失");
        TELEGRAPH_MAP.put("禾", "0243");
        CODE_TO_CHAR.put("0243", "禾");
        TELEGRAPH_MAP.put("丘", "0244");
        CODE_TO_CHAR.put("0244", "丘");
        TELEGRAPH_MAP.put("付", "0245");
        CODE_TO_CHAR.put("0245", "付");
        TELEGRAPH_MAP.put("仗", "0246");
        CODE_TO_CHAR.put("0246", "仗");
        TELEGRAPH_MAP.put("代", "0247");
        CODE_TO_CHAR.put("0247", "代");
        TELEGRAPH_MAP.put("仙", "0248");
        CODE_TO_CHAR.put("0248", "仙");
        TELEGRAPH_MAP.put("们", "0249");
        CODE_TO_CHAR.put("0249", "们");
        TELEGRAPH_MAP.put("仪", "0250");
        CODE_TO_CHAR.put("0250", "仪");
        TELEGRAPH_MAP.put("白", "0251");
        CODE_TO_CHAR.put("0251", "白");
        TELEGRAPH_MAP.put("仔", "0252");
        CODE_TO_CHAR.put("0252", "仔");
        TELEGRAPH_MAP.put("他", "0253");
        CODE_TO_CHAR.put("0253", "他");
        TELEGRAPH_MAP.put("斥", "0254");
        CODE_TO_CHAR.put("0254", "斥");
        TELEGRAPH_MAP.put("瓜", "0255");
        CODE_TO_CHAR.put("0255", "瓜");
        TELEGRAPH_MAP.put("乎", "0256");
        CODE_TO_CHAR.put("0256", "乎");
        TELEGRAPH_MAP.put("丛", "0257");
        CODE_TO_CHAR.put("0257", "丛");
        TELEGRAPH_MAP.put("令", "0258");
        CODE_TO_CHAR.put("0258", "令");
        TELEGRAPH_MAP.put("用", "0259");
        CODE_TO_CHAR.put("0259", "用");
        TELEGRAPH_MAP.put("甩", "0260");
        CODE_TO_CHAR.put("0260", "甩");
        TELEGRAPH_MAP.put("印", "0261");
        CODE_TO_CHAR.put("0261", "印");
        TELEGRAPH_MAP.put("乐", "0262");
        CODE_TO_CHAR.put("0262", "乐");
        TELEGRAPH_MAP.put("句", "0263");
        CODE_TO_CHAR.put("0263", "句");
        TELEGRAPH_MAP.put("匆", "0264");
        CODE_TO_CHAR.put("0264", "匆");
        TELEGRAPH_MAP.put("册", "0265");
        CODE_TO_CHAR.put("0265", "册");
        TELEGRAPH_MAP.put("外", "0266");
        CODE_TO_CHAR.put("0266", "外");
        TELEGRAPH_MAP.put("处", "0267");
        CODE_TO_CHAR.put("0267", "处");
        TELEGRAPH_MAP.put("冬", "0268");
        CODE_TO_CHAR.put("0268", "冬");
        TELEGRAPH_MAP.put("鸟", "0269");
        CODE_TO_CHAR.put("0269", "鸟");
        TELEGRAPH_MAP.put("务", "0270");
        CODE_TO_CHAR.put("0270", "务");
        TELEGRAPH_MAP.put("包", "0271");
        CODE_TO_CHAR.put("0271", "包");
        TELEGRAPH_MAP.put("主", "0272");
        CODE_TO_CHAR.put("0272", "主");
        TELEGRAPH_MAP.put("市", "0273");
        CODE_TO_CHAR.put("0273", "市");
        TELEGRAPH_MAP.put("立", "0274");
        CODE_TO_CHAR.put("0274", "立");
        TELEGRAPH_MAP.put("闪", "0275");
        CODE_TO_CHAR.put("0275", "闪");
        TELEGRAPH_MAP.put("兰", "0276");
        CODE_TO_CHAR.put("0276", "兰");
        TELEGRAPH_MAP.put("半", "0277");
        CODE_TO_CHAR.put("0277", "半");
        TELEGRAPH_MAP.put("汁", "0278");
        CODE_TO_CHAR.put("0278", "汁");
        TELEGRAPH_MAP.put("汇", "0279");
        CODE_TO_CHAR.put("0279", "汇");
        TELEGRAPH_MAP.put("头", "0280");
        CODE_TO_CHAR.put("0280", "头");
        TELEGRAPH_MAP.put("宁", "0281");
        CODE_TO_CHAR.put("0281", "宁");
        TELEGRAPH_MAP.put("它", "0282");
        CODE_TO_CHAR.put("0282", "它");
        TELEGRAPH_MAP.put("讨", "0283");
        CODE_TO_CHAR.put("0283", "讨");
        TELEGRAPH_MAP.put("写", "0284");
        CODE_TO_CHAR.put("0284", "写");
        TELEGRAPH_MAP.put("让", "0285");
        CODE_TO_CHAR.put("0285", "让");
        TELEGRAPH_MAP.put("礼", "0286");
        CODE_TO_CHAR.put("0286", "礼");
        TELEGRAPH_MAP.put("训", "0287");
        CODE_TO_CHAR.put("0287", "训");
        TELEGRAPH_MAP.put("议", "0288");
        CODE_TO_CHAR.put("0288", "议");
        TELEGRAPH_MAP.put("必", "0289");
        CODE_TO_CHAR.put("0289", "必");
        TELEGRAPH_MAP.put("讯", "0290");
        CODE_TO_CHAR.put("0290", "讯");
        TELEGRAPH_MAP.put("记", "0291");
        CODE_TO_CHAR.put("0291", "记");
        TELEGRAPH_MAP.put("永", "0292");
        CODE_TO_CHAR.put("0292", "永");
        TELEGRAPH_MAP.put("司", "0293");
        CODE_TO_CHAR.put("0293", "司");
        TELEGRAPH_MAP.put("尼", "0294");
        CODE_TO_CHAR.put("0294", "尼");
        TELEGRAPH_MAP.put("民", "0295");
        CODE_TO_CHAR.put("0295", "民");
        TELEGRAPH_MAP.put("弗", "0296");
        CODE_TO_CHAR.put("0296", "弗");
        TELEGRAPH_MAP.put("弘", "0297");
        CODE_TO_CHAR.put("0297", "弘");
        TELEGRAPH_MAP.put("出", "0298");
        CODE_TO_CHAR.put("0298", "出");
        TELEGRAPH_MAP.put("辽", "0299");
        CODE_TO_CHAR.put("0299", "辽");
        TELEGRAPH_MAP.put("奶", "0300");
        CODE_TO_CHAR.put("0300", "奶");
        TELEGRAPH_MAP.put("奴", "0301");
        CODE_TO_CHAR.put("0301", "奴");
        TELEGRAPH_MAP.put("召", "0302");
        CODE_TO_CHAR.put("0302", "召");
        TELEGRAPH_MAP.put("加", "0303");
        CODE_TO_CHAR.put("0303", "加");
        TELEGRAPH_MAP.put("皮", "0304");
        CODE_TO_CHAR.put("0304", "皮");
        TELEGRAPH_MAP.put("边", "0305");
        CODE_TO_CHAR.put("0305", "边");
        TELEGRAPH_MAP.put("发", "0306");
        CODE_TO_CHAR.put("0306", "发");
        TELEGRAPH_MAP.put("孕", "0307");
        CODE_TO_CHAR.put("0307", "孕");
        TELEGRAPH_MAP.put("圣", "0308");
        CODE_TO_CHAR.put("0308", "圣");
        TELEGRAPH_MAP.put("对", "0309");
        CODE_TO_CHAR.put("0309", "对");
        TELEGRAPH_MAP.put("台", "0310");
        CODE_TO_CHAR.put("0310", "台");
        TELEGRAPH_MAP.put("矛", "0311");
        CODE_TO_CHAR.put("0311", "矛");
        TELEGRAPH_MAP.put("纠", "0312");
        CODE_TO_CHAR.put("0312", "纠");
        TELEGRAPH_MAP.put("母", "0313");
        CODE_TO_CHAR.put("0313", "母");
        TELEGRAPH_MAP.put("幼", "0314");
        CODE_TO_CHAR.put("0314", "幼");
        TELEGRAPH_MAP.put("丝", "0315");
        CODE_TO_CHAR.put("0315", "丝");
        TELEGRAPH_MAP.put("邦", "0316");
        CODE_TO_CHAR.put("0316", "邦");
        TELEGRAPH_MAP.put("式", "0317");
        CODE_TO_CHAR.put("0317", "式");
        TELEGRAPH_MAP.put("迂", "0318");
        CODE_TO_CHAR.put("0318", "迂");
        TELEGRAPH_MAP.put("刑", "0319");
        CODE_TO_CHAR.put("0319", "刑");
        TELEGRAPH_MAP.put("戎", "0320");
        CODE_TO_CHAR.put("0320", "戎");
        TELEGRAPH_MAP.put("动", "0321");
        CODE_TO_CHAR.put("0321", "动");
        TELEGRAPH_MAP.put("扛", "0322");
        CODE_TO_CHAR.put("0322", "扛");
        TELEGRAPH_MAP.put("寺", "0323");
        CODE_TO_CHAR.put("0323", "寺");
        TELEGRAPH_MAP.put("吉", "0324");
        CODE_TO_CHAR.put("0324", "吉");
        TELEGRAPH_MAP.put("扣", "0325");
        CODE_TO_CHAR.put("0325", "扣");
        TELEGRAPH_MAP.put("考", "0326");
        CODE_TO_CHAR.put("0326", "考");
        TELEGRAPH_MAP.put("托", "0327");
        CODE_TO_CHAR.put("0327", "托");
        TELEGRAPH_MAP.put("老", "0328");
        CODE_TO_CHAR.put("0328", "老");
        TELEGRAPH_MAP.put("巩", "0329");
        CODE_TO_CHAR.put("0329", "巩");
        TELEGRAPH_MAP.put("执", "0330");
        CODE_TO_CHAR.put("0330", "执");
        TELEGRAPH_MAP.put("扩", "0332");
        CODE_TO_CHAR.put("0332", "扩");
        TELEGRAPH_MAP.put("扫", "0333");
        CODE_TO_CHAR.put("0333", "扫");
        TELEGRAPH_MAP.put("扬", "0334");
        CODE_TO_CHAR.put("0334", "扬");
        TELEGRAPH_MAP.put("地", "0335");
        CODE_TO_CHAR.put("0335", "地");
        TELEGRAPH_MAP.put("场", "0336");
        CODE_TO_CHAR.put("0336", "场");
        TELEGRAPH_MAP.put("耳", "0337");
        CODE_TO_CHAR.put("0337", "耳");
        TELEGRAPH_MAP.put("共", "0338");
        CODE_TO_CHAR.put("0338", "共");
        TELEGRAPH_MAP.put("芒", "0339");
        CODE_TO_CHAR.put("0339", "芒");
        TELEGRAPH_MAP.put("亚", "0340");
        CODE_TO_CHAR.put("0340", "亚");
        TELEGRAPH_MAP.put("芝", "0341");
        CODE_TO_CHAR.put("0341", "芝");
        TELEGRAPH_MAP.put("朽", "0342");
        CODE_TO_CHAR.put("0342", "朽");
        TELEGRAPH_MAP.put("朴", "0343");
        CODE_TO_CHAR.put("0343", "朴");
        TELEGRAPH_MAP.put("机", "0344");
        CODE_TO_CHAR.put("0344", "机");
        TELEGRAPH_MAP.put("权", "0345");
        CODE_TO_CHAR.put("0345", "权");
        TELEGRAPH_MAP.put("过", "0346");
        CODE_TO_CHAR.put("0346", "过");
        TELEGRAPH_MAP.put("臣", "0347");
        CODE_TO_CHAR.put("0347", "臣");
        TELEGRAPH_MAP.put("再", "0348");
        CODE_TO_CHAR.put("0348", "再");
        TELEGRAPH_MAP.put("协", "0349");
        CODE_TO_CHAR.put("0349", "协");
        TELEGRAPH_MAP.put("西", "0350");
        CODE_TO_CHAR.put("0350", "西");
        TELEGRAPH_MAP.put("压", "0351");
        CODE_TO_CHAR.put("0351", "压");
        TELEGRAPH_MAP.put("厌", "0352");
        CODE_TO_CHAR.put("0352", "厌");
        TELEGRAPH_MAP.put("在", "0353");
        CODE_TO_CHAR.put("0353", "在");
        TELEGRAPH_MAP.put("百", "0354");
        CODE_TO_CHAR.put("0354", "百");
        TELEGRAPH_MAP.put("有", "0355");
        CODE_TO_CHAR.put("0355", "有");
        TELEGRAPH_MAP.put("存", "0356");
        CODE_TO_CHAR.put("0356", "存");
        TELEGRAPH_MAP.put("而", "0357");
        CODE_TO_CHAR.put("0357", "而");
        TELEGRAPH_MAP.put("页", "0358");
        CODE_TO_CHAR.put("0358", "页");
        TELEGRAPH_MAP.put("匠", "0359");
        CODE_TO_CHAR.put("0359", "匠");
        TELEGRAPH_MAP.put("夸", "0360");
        CODE_TO_CHAR.put("0360", "夸");
        TELEGRAPH_MAP.put("夺", "0361");
        CODE_TO_CHAR.put("0361", "夺");
        TELEGRAPH_MAP.put("灰", "0362");
        CODE_TO_CHAR.put("0362", "灰");
        TELEGRAPH_MAP.put("达", "0363");
        CODE_TO_CHAR.put("0363", "达");
        TELEGRAPH_MAP.put("列", "0364");
        CODE_TO_CHAR.put("0364", "列");
        TELEGRAPH_MAP.put("死", "0365");
        CODE_TO_CHAR.put("0365", "死");
        TELEGRAPH_MAP.put("成", "0366");
        CODE_TO_CHAR.put("0366", "成");
        TELEGRAPH_MAP.put("夹", "0367");
        CODE_TO_CHAR.put("0367", "夹");
        TELEGRAPH_MAP.put("轨", "0368");
        CODE_TO_CHAR.put("0368", "轨");
        TELEGRAPH_MAP.put("邪", "0369");
        CODE_TO_CHAR.put("0369", "邪");
        TELEGRAPH_MAP.put("划", "0370");
        CODE_TO_CHAR.put("0370", "划");
        TELEGRAPH_MAP.put("迈", "0371");
        CODE_TO_CHAR.put("0371", "迈");
        TELEGRAPH_MAP.put("毕", "0372");
        CODE_TO_CHAR.put("0372", "毕");
        TELEGRAPH_MAP.put("至", "0373");
        CODE_TO_CHAR.put("0373", "至");
        TELEGRAPH_MAP.put("此", "0374");
        CODE_TO_CHAR.put("0374", "此");
        TELEGRAPH_MAP.put("贞", "0375");
        CODE_TO_CHAR.put("0375", "贞");
        TELEGRAPH_MAP.put("师", "0376");
        CODE_TO_CHAR.put("0376", "师");
        TELEGRAPH_MAP.put("尘", "0377");
        CODE_TO_CHAR.put("0377", "尘");
        TELEGRAPH_MAP.put("尖", "0378");
        CODE_TO_CHAR.put("0378", "尖");
        TELEGRAPH_MAP.put("劣", "0379");
        CODE_TO_CHAR.put("0379", "劣");
        TELEGRAPH_MAP.put("光", "0380");
        CODE_TO_CHAR.put("0380", "光");
        TELEGRAPH_MAP.put("当", "0381");
        CODE_TO_CHAR.put("0381", "当");
        TELEGRAPH_MAP.put("早", "0382");
        CODE_TO_CHAR.put("0382", "早");
        TELEGRAPH_MAP.put("吓", "0383");
        CODE_TO_CHAR.put("0383", "吓");
        TELEGRAPH_MAP.put("吐", "0384");
        CODE_TO_CHAR.put("0384", "吐");
        TELEGRAPH_MAP.put("虫", "0385");
        CODE_TO_CHAR.put("0385", "虫");
        TELEGRAPH_MAP.put("曲", "0386");
        CODE_TO_CHAR.put("0386", "曲");
        TELEGRAPH_MAP.put("团", "0387");
        CODE_TO_CHAR.put("0387", "团");
        TELEGRAPH_MAP.put("同", "0388");
        CODE_TO_CHAR.put("0388", "同");
        TELEGRAPH_MAP.put("吊", "0389");
        CODE_TO_CHAR.put("0389", "吊");
        TELEGRAPH_MAP.put("吃", "0390");
        CODE_TO_CHAR.put("0390", "吃");
        TELEGRAPH_MAP.put("因", "0391");
        CODE_TO_CHAR.put("0391", "因");
        TELEGRAPH_MAP.put("吸", "0392");
        CODE_TO_CHAR.put("0392", "吸");
        TELEGRAPH_MAP.put("吗", "0393");
        CODE_TO_CHAR.put("0393", "吗");
        TELEGRAPH_MAP.put("屿", "0394");
        CODE_TO_CHAR.put("0394", "屿");
        TELEGRAPH_MAP.put("帆", "0395");
        CODE_TO_CHAR.put("0395", "帆");
        TELEGRAPH_MAP.put("岁", "0396");
        CODE_TO_CHAR.put("0396", "岁");
        TELEGRAPH_MAP.put("回", "0397");
        CODE_TO_CHAR.put("0397", "回");
        TELEGRAPH_MAP.put("岂", "0398");
        CODE_TO_CHAR.put("0398", "岂");
        TELEGRAPH_MAP.put("刚", "0399");
        CODE_TO_CHAR.put("0399", "刚");
        TELEGRAPH_MAP.put("则", "0400");
        CODE_TO_CHAR.put("0400", "则");
        TELEGRAPH_MAP.put("肉", "0401");
        CODE_TO_CHAR.put("0401", "肉");
        TELEGRAPH_MAP.put("网", "0402");
        CODE_TO_CHAR.put("0402", "网");
        TELEGRAPH_MAP.put("年", "0403");
        CODE_TO_CHAR.put("0403", "年");
        TELEGRAPH_MAP.put("朱", "0404");
        CODE_TO_CHAR.put("0404", "朱");
        TELEGRAPH_MAP.put("先", "0405");
        CODE_TO_CHAR.put("0405", "先");
        TELEGRAPH_MAP.put("丢", "0406");
        CODE_TO_CHAR.put("0406", "丢");
        TELEGRAPH_MAP.put("舌", "0407");
        CODE_TO_CHAR.put("0407", "舌");
        TELEGRAPH_MAP.put("竹", "0408");
        CODE_TO_CHAR.put("0408", "竹");
        TELEGRAPH_MAP.put("迁", "0409");
        CODE_TO_CHAR.put("0409", "迁");
        TELEGRAPH_MAP.put("乔", "0410");
        CODE_TO_CHAR.put("0410", "乔");
        TELEGRAPH_MAP.put("伟", "0411");
        CODE_TO_CHAR.put("0411", "伟");
        TELEGRAPH_MAP.put("传", "0412");
        CODE_TO_CHAR.put("0412", "传");
        TELEGRAPH_MAP.put("乒", "0413");
        CODE_TO_CHAR.put("0413", "乒");
        TELEGRAPH_MAP.put("乓", "0414");
        CODE_TO_CHAR.put("0414", "乓");
        TELEGRAPH_MAP.put("休", "0415");
        CODE_TO_CHAR.put("0415", "休");
        TELEGRAPH_MAP.put("伍", "0416");
        CODE_TO_CHAR.put("0416", "伍");
        TELEGRAPH_MAP.put("伏", "0417");
        CODE_TO_CHAR.put("0417", "伏");
        TELEGRAPH_MAP.put("优", "0418");
        CODE_TO_CHAR.put("0418", "优");
        TELEGRAPH_MAP.put("伐", "0419");
        CODE_TO_CHAR.put("0419", "伐");
        TELEGRAPH_MAP.put("延", "0420");
        CODE_TO_CHAR.put("0420", "延");
        TELEGRAPH_MAP.put("件", "0421");
        CODE_TO_CHAR.put("0421", "件");
        TELEGRAPH_MAP.put("任", "0422");
        CODE_TO_CHAR.put("0422", "任");
        TELEGRAPH_MAP.put("伤", "0423");
        CODE_TO_CHAR.put("0423", "伤");
        TELEGRAPH_MAP.put("价", "0424");
        CODE_TO_CHAR.put("0424", "价");
        TELEGRAPH_MAP.put("份", "0425");
        CODE_TO_CHAR.put("0425", "份");
        TELEGRAPH_MAP.put("华", "0426");
        CODE_TO_CHAR.put("0426", "华");
        TELEGRAPH_MAP.put("仰", "0427");
        CODE_TO_CHAR.put("0427", "仰");
        TELEGRAPH_MAP.put("仿", "0428");
        CODE_TO_CHAR.put("0428", "仿");
        TELEGRAPH_MAP.put("伙", "0429");
        CODE_TO_CHAR.put("0429", "伙");
        TELEGRAPH_MAP.put("伪", "0430");
        CODE_TO_CHAR.put("0430", "伪");
        TELEGRAPH_MAP.put("自", "0431");
        CODE_TO_CHAR.put("0431", "自");
        TELEGRAPH_MAP.put("血", "0432");
        CODE_TO_CHAR.put("0432", "血");
        TELEGRAPH_MAP.put("向", "0433");
        CODE_TO_CHAR.put("0433", "向");
        TELEGRAPH_MAP.put("似", "0434");
        CODE_TO_CHAR.put("0434", "似");
        TELEGRAPH_MAP.put("后", "0435");
        CODE_TO_CHAR.put("0435", "后");
        TELEGRAPH_MAP.put("行", "0436");
        CODE_TO_CHAR.put("0436", "行");
        TELEGRAPH_MAP.put("舟", "0437");
        CODE_TO_CHAR.put("0437", "舟");
        TELEGRAPH_MAP.put("全", "0438");
        CODE_TO_CHAR.put("0438", "全");
        TELEGRAPH_MAP.put("会", "0439");
        CODE_TO_CHAR.put("0439", "会");
        TELEGRAPH_MAP.put("杀", "0440");
        CODE_TO_CHAR.put("0440", "杀");
        TELEGRAPH_MAP.put("合", "0441");
        CODE_TO_CHAR.put("0441", "合");
        TELEGRAPH_MAP.put("兆", "0442");
        CODE_TO_CHAR.put("0442", "兆");
        TELEGRAPH_MAP.put("企", "0443");
        CODE_TO_CHAR.put("0443", "企");
        TELEGRAPH_MAP.put("众", "0444");
        CODE_TO_CHAR.put("0444", "众");
        TELEGRAPH_MAP.put("爷", "0445");
        CODE_TO_CHAR.put("0445", "爷");
        TELEGRAPH_MAP.put("伞", "0446");
        CODE_TO_CHAR.put("0446", "伞");
        TELEGRAPH_MAP.put("创", "0447");
        CODE_TO_CHAR.put("0447", "创");
        TELEGRAPH_MAP.put("肌", "0448");
        CODE_TO_CHAR.put("0448", "肌");
        TELEGRAPH_MAP.put("朵", "0449");
        CODE_TO_CHAR.put("0449", "朵");
        TELEGRAPH_MAP.put("杂", "0450");
        CODE_TO_CHAR.put("0450", "杂");
        TELEGRAPH_MAP.put("危", "0451");
        CODE_TO_CHAR.put("0451", "危");
        TELEGRAPH_MAP.put("旬", "0452");
        CODE_TO_CHAR.put("0452", "旬");
        TELEGRAPH_MAP.put("旨", "0453");
        CODE_TO_CHAR.put("0453", "旨");
        TELEGRAPH_MAP.put("负", "0454");
        CODE_TO_CHAR.put("0454", "负");
        TELEGRAPH_MAP.put("各", "0455");
        CODE_TO_CHAR.put("0455", "各");
        TELEGRAPH_MAP.put("名", "0456");
        CODE_TO_CHAR.put("0456", "名");
        TELEGRAPH_MAP.put("多", "0457");
        CODE_TO_CHAR.put("0457", "多");
        TELEGRAPH_MAP.put("争", "0458");
        CODE_TO_CHAR.put("0458", "争");
        TELEGRAPH_MAP.put("色", "0459");
        CODE_TO_CHAR.put("0459", "色");
        TELEGRAPH_MAP.put("壮", "0460");
        CODE_TO_CHAR.put("0460", "壮");
        TELEGRAPH_MAP.put("冲", "0461");
        CODE_TO_CHAR.put("0461", "冲");
        TELEGRAPH_MAP.put("冰", "0462");
        CODE_TO_CHAR.put("0462", "冰");
        TELEGRAPH_MAP.put("庄", "0463");
        CODE_TO_CHAR.put("0463", "庄");
        TELEGRAPH_MAP.put("庆", "0464");
        CODE_TO_CHAR.put("0464", "庆");
        TELEGRAPH_MAP.put("亦", "0465");
        CODE_TO_CHAR.put("0465", "亦");
        TELEGRAPH_MAP.put("刘", "0466");
        CODE_TO_CHAR.put("0466", "刘");
        TELEGRAPH_MAP.put("齐", "0467");
        CODE_TO_CHAR.put("0467", "齐");
        TELEGRAPH_MAP.put("交", "0468");
        CODE_TO_CHAR.put("0468", "交");
        TELEGRAPH_MAP.put("次", "0469");
        CODE_TO_CHAR.put("0469", "次");
        TELEGRAPH_MAP.put("衣", "0470");
        CODE_TO_CHAR.put("0470", "衣");
        TELEGRAPH_MAP.put("产", "0471");
        CODE_TO_CHAR.put("0471", "产");
        TELEGRAPH_MAP.put("决", "0472");
        CODE_TO_CHAR.put("0472", "决");
        TELEGRAPH_MAP.put("充", "0473");
        CODE_TO_CHAR.put("0473", "充");
        TELEGRAPH_MAP.put("妄", "0474");
        CODE_TO_CHAR.put("0474", "妄");
        TELEGRAPH_MAP.put("闭", "0475");
        CODE_TO_CHAR.put("0475", "闭");
        TELEGRAPH_MAP.put("问", "0476");
        CODE_TO_CHAR.put("0476", "问");
        TELEGRAPH_MAP.put("闯", "0477");
        CODE_TO_CHAR.put("0477", "闯");
        TELEGRAPH_MAP.put("羊", "0478");
        CODE_TO_CHAR.put("0478", "羊");
        TELEGRAPH_MAP.put("并", "0479");
        CODE_TO_CHAR.put("0479", "并");
        TELEGRAPH_MAP.put("关", "0480");
        CODE_TO_CHAR.put("0480", "关");
        TELEGRAPH_MAP.put("米", "0481");
        CODE_TO_CHAR.put("0481", "米");
        TELEGRAPH_MAP.put("灯", "0482");
        CODE_TO_CHAR.put("0482", "灯");
        TELEGRAPH_MAP.put("州", "0483");
        CODE_TO_CHAR.put("0483", "州");
        TELEGRAPH_MAP.put("汗", "0484");
        CODE_TO_CHAR.put("0484", "汗");
        TELEGRAPH_MAP.put("污", "0485");
        CODE_TO_CHAR.put("0485", "污");
        TELEGRAPH_MAP.put("江", "0486");
        CODE_TO_CHAR.put("0486", "江");
        TELEGRAPH_MAP.put("池", "0487");
        CODE_TO_CHAR.put("0487", "池");
        TELEGRAPH_MAP.put("汤", "0488");
        CODE_TO_CHAR.put("0488", "汤");
        TELEGRAPH_MAP.put("忙", "0489");
        CODE_TO_CHAR.put("0489", "忙");
        TELEGRAPH_MAP.put("兴", "0490");
        CODE_TO_CHAR.put("0490", "兴");
        TELEGRAPH_MAP.put("宇", "0491");
        CODE_TO_CHAR.put("0491", "宇");
        TELEGRAPH_MAP.put("守", "0492");
        CODE_TO_CHAR.put("0492", "守");
        TELEGRAPH_MAP.put("宅", "0493");
        CODE_TO_CHAR.put("0493", "宅");
        TELEGRAPH_MAP.put("字", "0494");
        CODE_TO_CHAR.put("0494", "字");
        TELEGRAPH_MAP.put("安", "0495");
        CODE_TO_CHAR.put("0495", "安");
        TELEGRAPH_MAP.put("讲", "0496");
        CODE_TO_CHAR.put("0496", "讲");
        TELEGRAPH_MAP.put("军", "0497");
        CODE_TO_CHAR.put("0497", "军");
        TELEGRAPH_MAP.put("许", "0498");
        CODE_TO_CHAR.put("0498", "许");
        TELEGRAPH_MAP.put("论", "0499");
        CODE_TO_CHAR.put("0499", "论");
        TELEGRAPH_MAP.put("农", "0500");
        CODE_TO_CHAR.put("0500", "农");
    }

    // ==================== 1. 凯撒密码 ====================
    public static String caesarEncrypt(String text, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                sb.append((char) ((c - base + shift) % 26 + base));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String caesarDecrypt(String text, int shift) {
        return caesarEncrypt(text, 26 - (shift % 26));
    }

    // ==================== 2. 栅栏密码 ====================
    public static String railFenceEncrypt(String text, int rails) {
        if (rails < 2) return text;
        StringBuilder[] fence = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) fence[i] = new StringBuilder();
        int rail = 0;
        boolean down = true;
        for (char c : text.toCharArray()) {
            fence[rail].append(c);
            if (down) {
                rail++;
                if (rail == rails - 1) down = false;
            } else {
                rail--;
                if (rail == 0) down = true;
            }
        }
        StringBuilder result = new StringBuilder();
        for (StringBuilder sb : fence) result.append(sb);
        return result.toString();
    }

    public static String railFenceDecrypt(String text, int rails) {
        if (rails < 2) return text;
        int[] lengths = new int[rails];
        int rail = 0;
        boolean down = true;
        for (int i = 0; i < text.length(); i++) {
            lengths[rail]++;
            if (down) {
                rail++;
                if (rail == rails - 1) down = false;
            } else {
                rail--;
                if (rail == 0) down = true;
            }
        }
        String[] parts = new String[rails];
        int pos = 0;
        for (int i = 0; i < rails; i++) {
            parts[i] = text.substring(pos, pos + lengths[i]);
            pos += lengths[i];
        }
        int[] indices = new int[rails];
        StringBuilder result = new StringBuilder();
        rail = 0;
        down = true;
        for (int i = 0; i < text.length(); i++) {
            result.append(parts[rail].charAt(indices[rail]++));
            if (down) {
                rail++;
                if (rail == rails - 1) down = false;
            } else {
                rail--;
                if (rail == 0) down = true;
            }
        }
        return result.toString();
    }

    // ==================== 3. 倒序 ====================
    public static String reverse(String text) {
        return new StringBuilder(text).reverse().toString();
    }

    // ==================== 4. QWE密码 ====================
    public static String qweEncrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append(QWERTY.charAt(c - 'A'));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String qweDecrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            int idx = QWERTY.indexOf(c);
            if (idx >= 0) {
                sb.append((char) ('A' + idx));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ==================== 5. 莫尔斯电码 ====================
    public static String morseEncrypt(String text, String dot, String dash, String sep) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                String code = MORSE_CODE[c - 'A'];
                if (sb.length() > 0) sb.append(sep);
                sb.append(code.replace(".", dot).replace("-", dash));
            } else if (c >= '0' && c <= '9') {
                String code = MORSE_CODE[c - '0' + 26];
                if (sb.length() > 0) sb.append(sep);
                sb.append(code.replace(".", dot).replace("-", dash));
            } else if (c == ' ') {
                sb.append(sep).append(sep);
            }
        }
        return sb.toString();
    }

    public static String morseDecrypt(String text, String dot, String dash, String sep) {
        String standard = text.replace(dot, ".").replace(dash, "-");
        StringBuilder sb = new StringBuilder();
        String[] words = standard.split(sep + sep);
        for (int w = 0; w < words.length; w++) {
            String word = words[w].trim();
            if (word.isEmpty()) continue;
            String[] letters = word.split(sep);
            for (String letter : letters) {
                letter = letter.trim();
                if (letter.isEmpty()) continue;
                for (int i = 0; i < MORSE_CODE.length; i++) {
                    if (MORSE_CODE[i].equals(letter)) {
                        if (i < 26) sb.append((char) ('A' + i));
                        else sb.append((char) ('0' + i - 26));
                        break;
                    }
                }
            }
            if (w < words.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    // ==================== 6. 键盘V密码 ====================
    public static String keyboardVEncrypt(String text) {
        StringBuilder sb = new StringBuilder();
        outer:
        for (char c : text.toUpperCase().toCharArray()) {
            for (int i = 0; i < KEYBOARD_V_COLS.length; i++) {
                if (KEYBOARD_V_COLS[i].indexOf(c) >= 0) {
                    sb.append(i);
                    continue outer;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String keyboardVDecrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= '0' && c <= '9') {
                int idx = c - '0';
                if (idx < KEYBOARD_V_COLS.length) {
                    sb.append(KEYBOARD_V_COLS[idx].charAt(0));
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ==================== 7. 埃特巴什码 ====================
    public static String atbash(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append((char) ('Z' - (c - 'A')));
            } else if (c >= 'a' && c <= 'z') {
                sb.append((char) ('z' - (c - 'a')));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ==================== 8. 键盘坐标 ====================
    public static String keyboardCoordEncrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            for (int r = 0; r < KEYBOARD_ROWS.length; r++) {
                int col = KEYBOARD_ROWS[r].indexOf(c);
                if (col >= 0) {
                    sb.append(r + 1).append(col + 1);
                    break;
                }
            }
        }
        return sb.toString();
    }

    public static String keyboardCoordDecrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i += 2) {
            if (i + 1 < text.length()) {
                int r = text.charAt(i) - '1';
                int col = text.charAt(i + 1) - '1';
                if (r >= 0 && r < KEYBOARD_ROWS.length && col >= 0 && col < KEYBOARD_ROWS[r].length()) {
                    sb.append(KEYBOARD_ROWS[r].charAt(col));
                }
            }
        }
        return sb.toString();
    }

    // ==================== 9. 手机键盘 ====================
    public static String phoneKeyboardEncrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append(PHONE_KEYS.charAt(c - 'A'));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String phoneKeyboardDecrypt(String text) {
        StringBuilder sb = new StringBuilder();
        String map = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (char c : text.toCharArray()) {
            if (c >= '2' && c <= '9') {
                int idx = PHONE_KEYS.indexOf(c);
                sb.append(map.charAt(idx));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ==================== 10. 字母表 ====================
    public static String alphabetEncrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                if (sb.length() > 0) sb.append(" ");
                sb.append(c - 'A' + 1);
            }
        }
        return sb.toString();
    }

    public static String alphabetDecrypt(String text) {
        StringBuilder sb = new StringBuilder();
        String[] parts = text.split("\\s+");
        for (String part : parts) {
            try {
                int n = Integer.parseInt(part);
                if (n >= 1 && n <= 26) {
                    sb.append((char) ('A' + n - 1));
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return sb.toString();
    }

    // ==================== 11. 一个词暴力 ====================
    public static String bruteForceCaesar(String text) {
        StringBuilder sb = new StringBuilder();
        for (int shift = 1; shift <= 25; shift++) {
            sb.append("偏移").append(shift).append(": ");
            sb.append(caesarDecrypt(text, shift));
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    // ==================== 12. 维吉尼亚密码 ====================
    public static String vigenereEncrypt(String text, String key) {
        if (key == null || key.isEmpty()) return text;
        StringBuilder sb = new StringBuilder();
        key = key.toUpperCase();
        int keyLen = key.length();
        int j = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = key.charAt(j % keyLen) - 'A';
                sb.append((char) ((c - base + shift) % 26 + base));
                j++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String vigenereDecrypt(String text, String key) {
        if (key == null || key.isEmpty()) return text;
        StringBuilder sb = new StringBuilder();
        key = key.toUpperCase();
        int keyLen = key.length();
        int j = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = key.charAt(j % keyLen) - 'A';
                sb.append((char) ((c - base - shift + 26) % 26 + base));
                j++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ==================== 13. 元素周期表 ====================
    private static String getElementByFirstLetter(char c) {
        c = Character.toUpperCase(c);
        for (String el : ELEMENTS) {
            if (el.charAt(0) == c) {
                return el;
            }
        }
        return String.valueOf(c);
    }

    public static String elementEncrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(getElementByFirstLetter(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String elementDecrypt(String text) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            boolean found = false;
            if (i + 1 < text.length()) {
                String two = text.substring(i, i + 2);
                for (String el : ELEMENTS) {
                    if (el.equalsIgnoreCase(two)) {
                        sb.append(Character.toUpperCase(el.charAt(0)));
                        i += 2;
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                String one = text.substring(i, i + 1);
                for (String el : ELEMENTS) {
                    if (el.equalsIgnoreCase(one)) {
                        sb.append(Character.toUpperCase(el.charAt(0)));
                        i += 1;
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                sb.append(text.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }

    // ==================== 14. 增删空格 ====================
    public static String addSpaces(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(c).append(' ');
        }
        return sb.toString().trim();
    }

    public static String removeSpaces(String text) {
        return text.replaceAll("\\s+", "");
    }

    // ==================== 15. 替换字符 ====================
    public static String replaceChars(String text, String from, String to) {
        if (from == null || from.isEmpty() || to == null || to.isEmpty()) return text;
        return text.replace(from, to);
    }

    public static String restoreChars(String text, String from, String to) {
        return replaceChars(text, to, from);
    }

    // ==================== 16. 进制转换 ====================
    public static String radixConvert(String text, int fromBase, int toBase) {
        try {
            String[] parts = text.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                if (part.isEmpty()) continue;
                int val = Integer.parseInt(part, fromBase);
                sb.append(Integer.toString(val, toBase).toUpperCase()).append(" ");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            return "转换失败: " + e.getMessage();
        }
    }

    // ==================== 17. MD5 ====================
    public static String md5(String input) {
        return hash(input, "MD5");
    }

    private static String hash(String input, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // ==================== 18. 大小写转换 ====================
    public static String caseConvert(String text, boolean toUpper) {
        return toUpper ? text.toUpperCase() : text.toLowerCase();
    }

    // ==================== 19. 字母频率 ====================
    public static String letterFrequency(String text) {
        int[] freq = new int[26];
        int total = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                freq[Character.toUpperCase(c) - 'A']++;
                total++;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            if (freq[i] > 0) {
                char letter = (char) ('A' + i);
                double pct = total > 0 ? (freq[i] * 100.0 / total) : 0;
                sb.append(letter).append(": ").append(freq[i])
                  .append(" (").append(String.format("%.1f", pct)).append("%)\n");
            }
        }
        if (sb.length() == 0) return "无字母";
        return sb.toString().trim();
    }

    // ==================== 20. Base64 ====================
    public static String base64Encrypt(String input) {
        try {
            return Base64.encodeToString(input.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }

    public static String base64Decrypt(String input) {
        try {
            return new String(Base64.decode(input, Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {
            return "解码失败";
        }
    }

    // ==================== 21. Unicode ====================
    public static String unicodeEncrypt(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            sb.append(String.format("\\u%04x", (int) c));
        }
        return sb.toString();
    }

    public static String unicodeDecrypt(String input) {
        StringBuilder sb = new StringBuilder();
        String[] parts = input.split("\\\\u");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0 && !parts[i].isEmpty()) {
                sb.append(parts[i]);
                continue;
            }
            if (parts[i].length() >= 4) {
                try {
                    int code = Integer.parseInt(parts[i].substring(0, 4), 16);
                    sb.append((char) code);
                    sb.append(parts[i].substring(4));
                } catch (NumberFormatException e) {
                    sb.append("\\u").append(parts[i]);
                }
            } else {
                sb.append("\\u").append(parts[i]);
            }
        }
        return sb.toString();
    }

    // ==================== 22. 中文电码 ====================
    public static String chineseTelegraph(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            String code = TELEGRAPH_MAP.get(String.valueOf(c));
            if (code != null) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(code);
            } else {
                if (sb.length() > 0) sb.append(" ");
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ==================== 23. 区位码 ====================
    public static String quWeiMaEncrypt(String text) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] bytes = text.getBytes("GB2312");
            for (int i = 0; i < bytes.length; i += 2) {
                int q = (bytes[i] & 0xFF) - 0xA0;
                int w = (bytes[i + 1] & 0xFF) - 0xA0;
                sb.append(String.format("%02d%02d", q, w));
            }
        } catch (Exception e) {
            return "编码失败";
        }
        return sb.toString();
    }

    public static String quWeiMaDecrypt(String text) {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < text.length(); i += 4) {
                if (i + 4 <= text.length()) {
                    int q = Integer.parseInt(text.substring(i, i + 2)) + 0xA0;
                    int w = Integer.parseInt(text.substring(i + 2, i + 4)) + 0xA0;
                    byte[] bytes = new byte[]{(byte) q, (byte) w};
                    sb.append(new String(bytes, "GB2312"));
                }
            }
        } catch (Exception e) {
            return "解码失败";
        }
        return sb.toString();
    }

    // ==================== 24. 二维码 ====================
    public static Bitmap generateQRCode(String text, int size) {
        return QRCodeEncoder.encode(text, size);
    }
}
