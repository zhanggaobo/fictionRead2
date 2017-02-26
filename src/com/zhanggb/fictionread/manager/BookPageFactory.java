package com.zhanggb.fictionread.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import com.zhanggb.fictionread.R;
import com.zhanggb.fictionread.model.Section;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-23
 * Time: 下午12:43
 * To change this template use File | Settings | File Templates.
 */
public class BookPageFactory {
    private File book_file = null;
    private MappedByteBuffer m_mbBuf = null;
    private long m_mbBufLen = 0;
    private long m_mbBufBegin = 0;
    private long m_mbBufEnd = 0;
    private String m_strCharsetName = "GBK";
    //    private String m_strCharsetName = "UTF-8";
    private Bitmap m_book_bg = null;
    private int mWidth;
    private int mHeight;

    private Vector<String> m_lines = new Vector<String>();

    private int m_fontSize = 24;
    private int m_textColor = Color.BLACK;
    private int m_backColor = 0xffff9e85; // 背景颜色
    private int marginWidth = 15; // 左右与边缘的距离
    private int marginHeight = 20; // 上下与边缘的距离

    private int mLineCount; // 每页可以显示的行数
    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽
    private boolean m_isfirstPage, m_islastPage;

    private boolean found = false;
    private String encoding = null;

    private Paint mPaint;

    private Context context;
    private PreferencesManager preferencesManager;
    // 正则表达式   按自己需要来更改
    private static final String patternStr = "第[\\d一二三四五六七八九十百千]+[章节集回]\\s*.{0,20}";

    public BookPageFactory(Context context, int w, int h) {
        this.context = context;
        preferencesManager = new PreferencesManager(context);
        int textBack = preferencesManager.getTextBack();
        if (textBack == 2) {
            m_textColor = context.getResources().getColor(R.color.TextColorWhite);
        }
        mWidth = w;
        mHeight = h;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        int textSize = preferencesManager.getTextSize();
        m_fontSize = getPx(textSize);
        mPaint.setTextSize(m_fontSize);
        mPaint.setColor(m_textColor);
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;
        mLineCount = (int) (mVisibleHeight / m_fontSize); // 可显示的行数
    }

    public void openBook(long beginRead, String strFilePath) throws IOException {
        book_file = new File(strFilePath);
        String charCode = guessFileEncoding(book_file, new nsDetector());
        m_strCharsetName = charCode.equals("GB2312") ? "GBK" : charCode;
        long lLen = book_file.length();
        m_mbBufBegin = beginRead;
        m_mbBufEnd = beginRead;
        m_mbBufLen = (int) lLen;
        m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, lLen);
    }


    protected byte[] readParagraphBack(int nFromPos) {
        int nEnd = nFromPos;
        int i;
        byte b0, b1;
        if (m_strCharsetName.equals("UTF-16LE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else if (m_strCharsetName.equals("UTF-16BE")) {
            i = nEnd - 2;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                b1 = m_mbBuf.get(i + 1);
                if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else {
            i = nEnd - 1;
            while (i > 0) {
                b0 = m_mbBuf.get(i);
                if (b0 == 0x0a && i != nEnd - 1) {
                    i++;
                    break;
                }
                i--;
            }
        }
        if (i < 0)
            i = 0;
        int nParaSize = nEnd - i;
        int j;
        byte[] buf = new byte[nParaSize];
        for (j = 0; j < nParaSize; j++) {
            buf[j] = m_mbBuf.get(i + j);
        }
        return buf;
    }


    // 读取上一段落
    protected byte[] readParagraphForward(int nFromPos) {
        int nStart = nFromPos;
        int i = nStart;
        byte b0, b1;
        // 根据编码格式判断换行
        if (m_strCharsetName.equals("UTF-16LE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if (m_strCharsetName.equals("UTF-16BE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        } else {
            while (i < m_mbBufLen) {
                b0 = m_mbBuf.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }
        int nParaSize = i - nStart;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = m_mbBuf.get(nFromPos + i);
        }
        return buf;
    }


    private String guessFileEncoding(File file, nsDetector det) throws IOException {
        det.Init(new nsICharsetDetectionObserver() {
            public void Notify(String charset) {
                encoding = charset;
                found = true;
            }
        });

        BufferedInputStream imp = new BufferedInputStream(new FileInputStream(file));
        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = false;

        while ((len = imp.read(buf, 0, buf.length)) != -1) {
            // Check if the stream is only ascii.
            isAscii = det.isAscii(buf, len);
            if (isAscii) {
                break;
            }
            // DoIt if non-ascii and not done yet.
            done = det.DoIt(buf, len, false);
            if (done) {
                break;
            }
        }
        imp.close();
        det.DataEnd();

        if (isAscii) {
            encoding = "ASCII";
            found = true;
        }

        if (!found) {
            String[] prob = det.getProbableCharsets();
            //这里将可能的字符集组合起来返回
            for (int i = 0; i < prob.length; i++) {
                if (i == 0) {
                    encoding = prob[i];
                } else {
                    encoding += "," + prob[i];
                }
            }

            if (prob.length > 0) {
                // 在没有发现情况下,也可以只取第一个可能的编码,这里返回的是一个可能的序列
                return encoding;
            } else {
                return null;
            }
        }
        return encoding;
    }

    protected Vector<String> pageDown() {
        String strParagraph = "";
        Vector<String> lines = new Vector<String>();
        while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
            byte[] paraBuf = readParagraphForward((int) m_mbBufEnd); // 读取一个段落
            m_mbBufEnd += paraBuf.length;
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String strReturn = "";
            if (strParagraph.indexOf("\r\n") != -1) {
                strReturn = "\r\n";
                strParagraph = strParagraph.replaceAll("\r\n", "");
            } else if (strParagraph.indexOf("\n") != -1) {
                strReturn = "\n";
                strParagraph = strParagraph.replaceAll("\n", "");
            }

            if (strParagraph.length() == 0) {
                lines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                lines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);
                if (lines.size() >= mLineCount) {
                    break;
                }
            }
            if (strParagraph.length() != 0) {
                try {
                    m_mbBufEnd -= (strParagraph + strReturn)
                            .getBytes(m_strCharsetName).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return lines;
    }

    protected void pageUp() {
        if (m_mbBufBegin < 0) {
            m_mbBufBegin = 0;
        }
        Vector<String> lines = new Vector<String>();
        String strParagraph = "";
        while (lines.size() < mLineCount && m_mbBufBegin > 0) {
            Vector<String> paraLines = new Vector<String>();
            byte[] paraBuf = readParagraphBack((int) m_mbBufBegin);
            m_mbBufBegin -= paraBuf.length;
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            strParagraph = strParagraph.replaceAll("\r\n", "");
            strParagraph = strParagraph.replaceAll("\n", "");

            if (strParagraph.length() == 0) {
                paraLines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                paraLines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);
            }
            lines.addAll(0, paraLines);
        }
        while (lines.size() > mLineCount) {
            try {
                m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
                lines.remove(0);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        m_mbBufEnd = m_mbBufBegin;
        return;
    }

    public void prePage() throws IOException {
        if (m_mbBufBegin <= 0) {
            m_mbBufBegin = 0;
            m_isfirstPage = true;
            return;
        } else {
            m_isfirstPage = false;
        }
        m_lines.clear();
        pageUp();
        m_lines = pageDown();
    }

    public void nextPage() throws IOException {
        if (m_mbBufEnd >= m_mbBufLen) {
            m_islastPage = true;
            return;
        } else m_islastPage = false;
        m_lines.clear();
        m_mbBufBegin = m_mbBufEnd;
        m_lines = pageDown();
    }

    public void onDraw(Canvas c) {
        if (m_lines.size() == 0)
            m_lines = pageDown();
        if (m_lines.size() > 0) {
            if (m_book_bg == null) {
                c.drawColor(m_backColor);
            } else {
                int w = m_book_bg.getWidth();
                int h = m_book_bg.getHeight();
                c.drawBitmap(m_book_bg, 0, 0, null);
            }
            int y = marginHeight;
            for (String strLine : m_lines) {
                y += m_fontSize;
                c.drawText(strLine, marginWidth, y, mPaint);
            }
        }
        float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
        DecimalFormat df = new DecimalFormat("#0.0");
        String strPercent = df.format(fPercent * 100) + "%";
        int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
        c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5, mPaint);
    }

    public List<Section> findSectionDirectory(String path) {
        List<Section> sections = new ArrayList<Section>();
        long bufEnd = 0;
        String strParagraph = "";
        while (bufEnd < m_mbBufLen) {
            byte[] paraBuf = readParagraphForward((int) bufEnd); // 读取一个段落
            bufEnd += paraBuf.length;
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String strReturn = "";
            if (strParagraph.indexOf("\r\n") != -1) {
                strReturn = "\r\n";
                strParagraph = strParagraph.replaceAll("\r\n", "");
            } else if (strParagraph.indexOf("\n") != -1) {
                strReturn = "\n";
                strParagraph = strParagraph.replaceAll("\n", "");
            }

            while (strParagraph.length() > 0) {
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
                String line = strParagraph.substring(0, nSize);
                strParagraph = strParagraph.substring(nSize);
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String title = line.substring(start, end);
                    Section section = new Section();
                    section.setName(title);
                    section.setCurrent(bufEnd - 80);
                    sections.add(section);
                }
            }
            if (strParagraph.length() != 0) {
                try {
                    bufEnd -= (strParagraph + strReturn).getBytes(m_strCharsetName).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return sections;
    }

    public void setBgBitmap(Bitmap BG) {
        m_book_bg = BG;
    }

    public long getM_mbBufBegin() {
        return m_mbBufBegin;
    }

    public long getM_mbBufBegin(int percent) {
        m_mbBufBegin = (m_mbBufLen / 100) * percent;
        return m_mbBufBegin;
    }


    public String getPercent() {
        float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
        DecimalFormat df = new DecimalFormat("#0");
        String strPercent = df.format(fPercent * 100) + "%";
        return strPercent;
    }

    public int getIntPercent() {
        float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
        return (int) (fPercent * 100);
    }

    public boolean isFirstPage() {
        return m_isfirstPage;
    }

    public boolean isLastPage() {
        return m_islastPage;
    }

    private int getPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dp);
    }
}
