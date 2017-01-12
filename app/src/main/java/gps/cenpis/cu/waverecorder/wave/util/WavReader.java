/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gps.cenpis.cu.waverecorder.wave.util;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author farias-i3
 */
public class WavReader {

    private double[] left;

    public double[] getLeft() {
        return left;
    }

    public void setLeft(double[] left) {
        this.left = left;
    }

    public double[] getRight() {
        return right;
    }

    public void setRight(double[] right) {
        this.right = right;
    }

    private double[] right;

    // convert two bytes to one double in the range -1 to 1
    static double bytesToDouble(byte firstByte, byte secondByte) {

        int fb = firstByte < 0 ? firstByte + 256 : firstByte;
        int sb = secondByte < 0 ? secondByte + 256 : secondByte;

        int s2 = ((sb << 8) | fb);
        s2 = s2 > 32768.0 ? s2 - 65536 : s2;

        // convert two bytes to one short (little endian)
        // short s = (short) ((secondByte << 8) | firstByte);
        // convert to range from -1 to (just below) 1
        double result = s2 / 32768.0;

        return result;
    }

    // Returns left and right double arrays. 'right' will be null if sound is mono.
    public void openWav(String filename) {

        byte[] wav = null;
        InputStream is = null;
        try {
            is = new FileInputStream(filename);
            wav = IOUtils.toByteArray(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        File f = new File(filename);
//        int size = (int)f.length();
//        byte[] wav = new byte[size];
//        InputStream in = null;
//        try {
//            in = new FileInputStream(filename);
//            in.read(wav);
//        } catch (IOException ex) {
//            Log.i(WavReader.class.getName(), ex.getMessage());
//        } finally {
//            if (in != null) try {
//                in.close();
//            } catch (Exception ex) {
//                Log.i(WavReader.class.getName(), ex.getMessage());
//            }
//        }

//        Path p = FileSystems.getDefault().getPath("", filename);
//        byte[] wav = null;
//
//        try {
//            wav = Files.readAllBytes(p);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        // Determine if mono or stereo
        int channels = wav[22];     // Forget byte 23 as 99.999% of WAVs are 1 or 2 channels

        // Get past all the other sub chunks to get to the data subchunk:
        int pos = 12;   // First Subchunk ID from 12 to 16

        // Keep iterating until we find the data chunk (i.e. 64 61 74 61 ...... (i.e. 100 97 116 97 in decimal))
        while (!(wav[pos] == 100 && wav[pos + 1] == 97 && wav[pos + 2] == 116 && wav[pos + 3] == 97)) {
            pos += 4;
            int chunkSize = wav[pos] + wav[pos + 1] * 256 + wav[pos + 2] * 65536 + wav[pos + 3] * 16777216;
            pos += 4 + chunkSize;
        }
        pos += 8;

        // Pos is now positioned to start of actual sound data.
        int samples = (wav.length - pos) / 2;     // 2 bytes per sample (16 bit sound mono)
        if (channels == 2) {
            samples /= 2;        // 4 bytes per sample (16 bit stereo)
        }
        // Allocate memory (right will be null if only mono sound)
        left = new double[samples];
        if (channels == 2) {
            right = new double[samples];
        } else {
            right = null;
        }

        // Write to double array/s:
        int i = 0;
        while (pos < wav.length) {
            left[i] = bytesToDouble(wav[pos], wav[pos + 1]);
            pos += 2;
            if (channels == 2) {
                right[i] = bytesToDouble(wav[pos], wav[pos + 1]);
                pos += 2;
            }
            i++;
        }
    }

    public static void main(String[] args) throws Exception {

        //String file = "C:/C170812.wav";
        //String file = "C:/y4.wav";
        String file = "C:/y5true.wav";

        WavReader wr = new WavReader();
        wr.openWav(file);

        double[] left = wr.getLeft();
        double[] right = wr.getRight();
    }
}
