package com.texturegarden.utilities;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public final class PNGReader {
//  final static boolean verbose = true;
//
//  private int ya;
//  private int pixels[];
//  private InputStream input_stream;
//  private CRC32 crc;
//  private Inflater inflater;
//  PNGMaker png_maker;
//  private int yc;
//  private int yd;
//  private long ye;
//
//  private static byte yf[];
//  public static final int lut1[] = { 0x49484452, 0x504c5445, 0x49444154, 0x49454e44, 0x624b4744, 0x73424954, 0x6348524d, 0x67414d41, 0x68495354, 0x70485973, 0x74524e53, 0x74494d45, 0x74455874, 0x7a545874 };
//  public static final byte lut2[] = { -119, 80, 78, 71, 13, 10, 26, 10 };
//  private Vector vector;
//  private boolean yj;
//  private boolean yk;
//  private boolean yl;
//  private boolean ym;
//  private int width;
//  private int height;
//  private int yp;
//  private int yq;
//  private int yr;
//  private int ys;
//  private int yt;
//  private int yu;
//  private int yv[];
//  private byte yw[];
//  public static final int xa = 512;
//  byte xb[];
//  int xc[];
//  int xd[];
//  private int gamma;
//  private static int xl[];
//  private static int xm[];
//
//  static String error;
//
//  static {
//    // debug("PNG_IN2");
//    if (verbose) {
//      error = "Error: ";
//    }
//
//    yf = new byte[8192];
//    xl = new int[256];
//    xm = new int[256];
//    // debug("PNG_IN2");
//  }
//
//  public PNGReader(InputStream input_stream_two) throws IOException {
//    // debug("PNG_IN InputStream = " + input_stream_two);
//    ya = 1167;
//    crc = new CRC32();
//    //yf = new byte[8192];
//    vector = new Vector();
//    yu = 1;
//    inflater = new Inflater();
//    gamma = 45000;
//
//    // gamma = 100000;
//    // changeGamma(gamma);
//    // xr();
//
//    //xl = new int[256];
//    //xm = new int[256];
//    input_stream = input_stream_two;
//    input_stream_two.read(yf, 0, 8);
//    for (int i = 0; i < 8; i++)
//      if (yf[i] != lut2[i]) {
//        throw error(verbose ? "Wrong signature" : "");
//      }
//
//    if (xn() == 13) {
//      input_stream_two.read(yf, 0, 21);
//      ye = 0xffffffffL & (long) xh(yf, 17);
//      crc.update(yf, 0, 17);
//      yd = xh(yf, 0);
//      if (yd == lut1[0] && ye == crc.getValue()) {
//        width = xh(yf, 4);
//        height = xh(yf, 8);
//        yp = 0xff & yf[12];
//        yq = 0xff & yf[13];
//        yr = 0xff & yf[14];
//        ys = 0xff & yf[15];
//        yt = 0xff & yf[16];
//        if (yq == 2) {
//          yu = 3;
//        }
//
//        if (yq == 4) {
//          yu = 2;
//        }
//
//        if (yq == 6) {
//          yu = 4;
//        }
//
//        yv = new int[yu];
//      } else {
//        throw error(verbose ? "Invalid IHDR chunk" : "");
//      }
//    } else {
//      throw error(verbose ? "Invalid IHDR chunk" : "");
//    }
//    if (yq == 3) {
//      for (int j = 0; j < 256; j++)
//        xm[j] = 255;
//    }
//    do {
//      yc = xn();
//      crc.reset();
//      input_stream_two.read(yf, 0, 4);
//      crc.update(yf, 0, 4);
//      yd = xh(yf, 0);
//      if (yk && yd != lut1[2])
//        yl = true;
//      switch (yd) {
//        case 1347179589 :
//          xo();
//          break;
//
//        case 1229209940 :
//          if (yk && !yl) {
//            xp();
//          } else {
//            if (!xq(lut1[7]) || (ya & 0x80) == 0) {
//              xr();
//            }
//            xs();
//          }
//          break;
//
//        case 1229278788 :
//          xt();
//          break;
//
//        case 1732332865 :
//          changeGamma();
//          break;
//
//        case 1951551059 :
//          xv();
//          break;
//
//        default :
//          xw();
//          break;
//      }
//    } while (yd != lut1[3]);
//    // debug("PNG_OUT");
//  }
//
//  private void xo() throws IOException {
//    if (yk || yj) {
//      throw error(verbose ? "PLTE order" : "");
//    }
//
//    yj = true;
//    vector.addElement(new Integer(yd));
//    if (yc > 8192) {
//      throw error(verbose ? "Implementation - too long colour map: " + yc : "");
//    }
//    yw = new byte[yc];
//    input_stream.read(yw, 0, yc);
//    ye = 0xffffffffL & (long) xn();
//    crc.update(yw, 0, yc);
//    if (ye != crc.getValue()) {
//      throw error(verbose ? "CRC32 check in PLTE" : "");
//    } else {
//      return;
//    }
//  }
//
//  private void xs() throws IOException {
//    int ai[] = { 0, 4, 0, 2, 0, 1, 0 };
//    int ai1[] = { 8, 8, 4, 4, 2, 2, 1 };
//    int ai2[] = { 0, 0, 4, 0, 2, 0, 1 };
//    int ai3[] = { 8, 8, 8, 4, 4, 2, 2 };
//    int ai4[] = new int[7];
//    int ai5[] = new int[7];
//    byte byte0 = yt != 1 ? ((byte) (1)) : 7;
//    if (byte0 == 7) {
//      for (int i = 0; i < 7; i++) {
//        ai4[i] = ((width + ai1[i]) - 1 - ai[i]) / ai1[i];
//        ai5[i] = ((height + ai3[i]) - 1 - ai2[i]) / ai3[i];
//      }
//    } else {
//      ai[0] = 0;
//      ai1[0] = 1;
//      ai2[0] = 0;
//      ai3[0] = 1;
//      ai4[0] = width;
//      ai5[0] = height;
//    }
//
//    // new code...
//    PixelArrays.ensureArray1Size(width * height);
//    pixels = PixelArrays.pixels1;
//
//    // old code...
//    // pixels = new int[width * height];
//    png_maker = new PNGMaker(input_stream, inflater, 512);
//    vector.addElement(new Integer(yd));
//    int j = (yu * yp + 7) / 8;
//    boolean flag = false;
//    xb = new byte[j * width + 1];
//    xc = new int[j * width];
//    xd = new int[j * width];
//    Object obj = null;
//    for (int l = 0; l < byte0; l++) {
//      int i1 = (ai4[l] * yp * yu + 7) / 8;
//      if (i1 != 0) {
//        for (int j1 = 0; j1 < i1; j1++) {
//          xc[j1] = 0;
//        }
//
//        for (int k1 = 0; k1 < ai5[l]; k1++) {
//          int k = png_maker.read();
//          if (k > 4)
//            throw error(verbose ? "Unknown filter" : "");
//          if (k == -1)
//            throw error(verbose ? "Early EOF in IDAT" : "");
//          int i2;
//          for (int l1 = 0; l1 < i1; l1 += i2) {
//            i2 = 0;
//            if ((i2 = png_maker.read(xb, l1, i1 - l1)) == -1)
//              throw error(verbose ? "Early EOF in IDAT" : "");
//          }
//
//          zn(k, j, i1, xb, xc, xd);
//          boolean flag1 = false;
//          boolean flag2 = false;
//          int l2 = ~(-1 << yp);
//          for (int i3 = 0; i3 < ai4[l]; i3++) {
//            int k2 = i3 * yp * yu;
//            int j2 = 8 - k2 % 8 - yp;
//            for (int j3 = 0; j3 < yu; j3++)
//              switch (yp) {
//                case 1 : // '1'
//                case 2 : // '2'
//                case 4 : // '4'
//                  yv[j3] = l2 & xd[k2 >> 3] >> j2;
//                  break;
//
//                case 8 : // '\b'
//                  yv[j3] = l2 & xd[(k2 >> 3) + j3];
//                  break;
//
//                case 16 : // '\020'
//                  yv[j3] = l2 & xd[(k2 >> 3) + (j3 << 1)];
//                  break;
//
//                default :
//                  throw error(verbose ? "bit depth " + yp : "");
//              }
//
//            pixels[width * (ai2[l] + k1 * ai3[l]) + ai[l] + i3 * ai1[l]] = zv(yv);
//          }
//
//          int ai6[] = xc;
//          xc = xd;
//          xd = ai6;
//        }
//      }
//    }
//  }
//
//  private int zv(int ai[]) throws IOException {
//    int i = yp;
//    int j = 0;
//    switch (yq) {
//      case 3 : // '3'
//        j |= xl[0xff & yw[3 * ai[0]]] << 16;
//        j |= xl[0xff & yw[3 * ai[0] + 1]] << 8;
//        j |= xl[0xff & yw[3 * ai[0] + 2]];
//        j |= xm[ai[0]] << 24;
//        break;
//
//      case 2 : // '2'
//        if (!ym)
//          j = 0xff000000;
//        else
//          j = ai[0] != xm[0] || ai[1] != xm[1] || ai[2] != xm[2] ? 0xff000000 : 0;
//        j |= xl[0xff & ai[0]] << 16;
//        j |= xl[0xff & ai[1]] << 8;
//        j |= xl[0xff & ai[2]];
//        break;
//
//      case 6 : // '6'
//        j = 0;
//        j |= xl[0xff & ai[0]] << 16;
//        j |= xl[0xff & ai[1]] << 8;
//        j |= xl[0xff & ai[2]];
//        j |= (0xff & ai[3]) << 24;
//        break;
//
//      case 0 : // '\0'
//        j = ai[0];
//        switch (yp) {
//          case 1 : // '1'
//            j = ai[0] != 0 ? xl[255] : 0;
//            j |= j << 8;
//            j |= (0xff00 & j) << 8;
//            break;
//
//          case 2 : // '2'
//            j |= j << i;
//            i <<= 1;
//            // fall through
//
//          case 4 : // '4'
//            j |= j << i;
//            // fall through
//
//          case 3 : // '3'
//          default :
//            j = xl[j];
//            j |= j << 8;
//            j |= (0xff00 & j) << 8;
//            break;
//        }
//        if (!ym)
//          j |= 0xff000000;
//        else
//          j |= ai[0] != xm[0] ? 0xff000000 : 0;
//        break;
//
//      case 4 : // '4'
//        j = xl[ai[0]];
//        j |= j << 8;
//        j |= (0xff00 & j) << 8;
//        j |= (0xff & ai[1]) << 24;
//        break;
//
//      case 1 : // '1'
//      case 5 : // '5'
//      default :
//        throw error(verbose ? "Colour type " + yq : "");
//    }
//
//    return j;
//  }
//
//  private void xt() throws IOException {
//    vector.addElement(new Integer(yd));
//    if (!yk || yq == 3 && !yj) {
//      throw error(verbose ? "IDAT or PLTE absence" : "");
//    }
//
//    ye = 0xffffffffL & (long) xn();
//    if (ye != crc.getValue()) {
//      throw error(verbose ? "CRC32 check in IEND" : "");
//    } else {
//      return;
//    }
//  }
//
//  private void changeGamma() throws IOException {
//    vector.addElement(new Integer(yd));
//    if (yj || yk) {
//      throw error(verbose ? "Late gAMA appeares" : "");
//    }
//    if ((ya & 0x80) == 0) {
//      xp();
//      return;
//    }
//
//    input_stream.read(yf, 0, 8);
//    crc.update(yf, 0, 4);
//    gamma = xh(yf, 0); // ?
//    ye = 0xffffffffL & (long) xh(yf, 4);
//    if (ye != crc.getValue()) {
//      throw error(verbose ? "CRC32 check in gAMA" : "");
//    } else {
//      xr();
//      return;
//    }
//  }
//
//  private void xv() throws IOException {
//    if (yq == 3 && !yj) {
//      throw error(verbose ? "Early tRNS appeared" : "");
//    }
//    vector.addElement(new Integer(yd));
//    if ((ya & 0x400) == 0) {
//      xp();
//      return;
//    }
//    ym = true;
//    input_stream.read(yf, 0, yc + 4);
//    crc.update(yf, 0, yc);
//    ye = 0xffffffffL & (long) xh(yf, yc);
//    if (ye != crc.getValue())
//      throw error(verbose ? "CRC32 check in tRNS" : "");
//    switch (yq) {
//      case 3 : // '3'
//        for (int i = 0; i < yc; i++)
//          xm[i] = 0xff & yf[i];
//
//        return;
//
//      case 0 : // '\0'
//      case 2 : // '2'
//        for (int j = 0; j < yu; j++)
//          xm[j] = zr(yf, yc + (j << 1));
//
//        return;
//
//      case 1 : // '1'
//      default :
//        throw error(verbose ? "Unsupported tRNS for colour type " + yq : "");
//    }
//  }
//
//  private void zn(int i, int j, int k, byte abyte0[], int ai[], int ai1[]) {
//    switch (i) {
//      case 0 : // '\0'
//        for (int l = 0; l < k; l++) {
//          ai1[l] = 0xff & abyte0[l];
//        }
//
//        return;
//
//      case 1 : // '1'
//        for (int i1 = 0; i1 < j; i1++) {
//          ai1[i1] = 0xff & abyte0[i1];
//        }
//
//        for (int j1 = j; j1 < k; j1++) {
//          ai1[j1] = 0xff & ai1[j1 - j] + (0xff & abyte0[j1]);
//        }
//
//        return;
//
//      case 2 : // '2'
//        for (int k1 = 0; k1 < k; k1++) {
//          ai1[k1] = 0xff & ai[k1] + (0xff & abyte0[k1]);
//        }
//
//        return;
//
//      case 3 : // '3'
//        for (int l1 = 0; l1 < j; l1++) {
//          ai1[l1] = 0xff & ai[l1] / 2 + (0xff & abyte0[l1]);
//        }
//
//        for (int i2 = j; i2 < k; i2++) {
//          ai1[i2] = 0xff & (ai[i2] + ai1[i2 - j]) / 2 + (0xff & abyte0[i2]);
//        }
//
//        return;
//
//      case 4 : // '4'
//        for (int j2 = 0; j2 < j; j2++) {
//          ai1[j2] = 0xff & (0xff & abyte0[j2]) + paeth(0, ai[j2], 0);
//        }
//
//        for (int k2 = j; k2 < k; k2++) {
//          ai1[k2] = 0xff & (0xff & abyte0[k2]) + paeth(ai1[k2 - j], ai[k2], ai[k2 - j]);
//        }
//
//        return;
//    }
//  }
//
//  private int paeth(int i, int j, int k) {
//    int l = (i + j) - k;
//    int i1 = l <= i ? i - l : l - i;
//    int j1 = l <= j ? j - l : l - j;
//    int k1 = l <= k ? k - l : l - k;
//
//    if (i1 <= j1 && i1 <= k1) {
//      return i;
//    }
//
//    if (j1 <= k1) {
//      return j;
//    } else {
//      return k;
//    }
//  }
//
//  private void xr() {
//    //double d;
//    //d = 45000D / (double)gamma;
//
//    xl[0] = 0;
//    for (int i = 1; i < 256; i++) {
//      double d1 = (double) i / 255D;
//      xl[i] = 0xff & (int) Math.round(255D * Math.exp(Math.log(d1))); // "d * " - inside (before Math)
//    }
//  }
//
//  public int getWidth() {
//    return width;
//  }
//
//  public int getHeight() {
//    return height;
//  }
//
//  // copies into a new array...
//  public int[] getPixels() {
//    int ai[] = new int[width * height];
//    System.arraycopy(pixels, 0, ai, 0, width * height);
//    return ai;
//  }
//
//  /*
//     public int getGamma() {
//        return gamma;
//     }
//  
//  
//     void changeGamma(int i) {
//        gamma = i;
//        xr();
//        for(int k = 0; k < pixels.length; k++)
//        {
//           int j = 0xff000000 & pixels[k];
//           for(int l = 0; l < 24; l += 8)
//              j |= xl[0xff & pixels[k] >> l] << l;
//  
//           pixels[k] = j;
//        }
//     }
//  */
//
//  private void xw() throws IOException {
//    if (yk) {
//      yl = true;
//    }
//    vector.addElement(new Integer(yd));
//    if ((yd & 0x20000000) != 0) {
//      xp();
//      return;
//    } else {
//      throw error(verbose ? "Unknown critical chunk " + yd : "");
//    }
//  }
//
//  private void xp() throws IOException {
//    boolean flag = false;
//    for (int j = 0; 8192 * j < yc; j++) {
//      int i = yc - 8192 * j < 8192 ? yc % 8192 : 8192;
//      if (input_stream.read(yf, 0, i) <= 0) {
//        throw errorEOF(verbose ? "Unexpected la??ut" : "");
//      }
//      crc.update(yf, 0, i);
//    }
//
//    ye = 0xffffffffL & (long) xn();
//    if (ye != crc.getValue()) {
//      throw error(verbose ? "CRC32 check in " + yd : "");
//    } else {
//      return;
//    }
//  }
//
//  private boolean xq(int i) {
//    boolean flag = false;
//    for (Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
//      if (((Integer) enumeration.nextElement()).intValue() == i) {
//        flag = true;
//        break;
//      }
//
//    return flag;
//  }
//
//  private int zs() throws IOException {
//    int i = input_stream.read() & 0xff;
//    return i << 8 | input_stream.read() & 0xff;
//  }
//
//  private int xn() throws IOException {
//    int i = zs();
//    return i << 16 | zs();
//  }
//
//  private int zr(byte abyte0[], int i) {
//    return (0xff & abyte0[i]) << 8 | 0xff & abyte0[i + 1];
//  }
//
//  private int xh(byte abyte0[], int i) {
//    return zr(abyte0, i) << 16 | zr(abyte0, i + 2);
//  }
//
//  static IOException error(String s) {
//    return new IOException(s);
//  }
//
//  static EOFException errorEOF(String s) {
//    return new EOFException(error + s);
//  }
//
//  public static void main(String[] args) {
//    // Rockz.main(null);
//  }
//
//  static void debug(String o) {
//    System.out.println(o);
//  }
//
//  private class PNGMaker extends InflaterInputStream {
//    private int xe;
//
//    public PNGMaker(InputStream input_stream_two, Inflater inflater, int i) {
//      super(input_stream_two, inflater, i);
//    }
//
//    protected void fill() throws IOException {
//      byte abyte0[] = new byte[8];
//      if (xe == 0 && yk) {
//        in.read(abyte0, 0, 8);
//        yc = xh(abyte0, 0);
//        yd = xh(abyte0, 4);
//        if (yd != PNGReader.lut1[2])
//          throw error(verbose ? "next not IDAT" + yd : "");
//        crc.reset();
//        crc.update(abyte0, 4, 4);
//      }
//      yk = true;
//      int i = yc - xe;
//      if (i > buf.length) {
//        len = in.read(buf, 0, buf.length);
//        if (len == -1) {
//          throw error(verbose ? "EOF in IDAT" : "");
//        } else {
//          crc.update(buf, 0, len);
//          xe += len;
//          inf.setInput(buf, 0, len);
//          return;
//        }
//      }
//      len = in.read(buf, 0, i);
//      if (len == -1)
//        throw error(verbose ? "EOF in IDAT" : "");
//      crc.update(buf, 0, len);
//      xe += len;
//      if (xe == yc) {
//        xe = 0;
//        in.read(abyte0, 0, 4);
//        ye = 0xffffffffL & (long) xh(abyte0, 0);
//        if (ye != crc.getValue())
//          throw error(verbose ? "CRC32 check in IDAT" : "");
//      }
//      inf.setInput(buf, 0, len);
//    }
//  }

}
