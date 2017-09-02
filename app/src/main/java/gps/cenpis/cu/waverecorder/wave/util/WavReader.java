/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created by Felipe Rodriguez Arias <ucifarias@gmail.com> on 12/01/2017
 */

package gps.cenpis.cu.waverecorder.wave.util;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    private static double bytesToDouble(byte firstByte, byte secondByte) {

        int fb = firstByte < 0 ? firstByte + 256 : firstByte;
        int sb = secondByte < 0 ? secondByte + 256 : secondByte;

        int s2 = ((sb << 8) | fb);
        s2 = s2 > 32768.0 ? s2 - 65536 : s2;

        // convert two bytes to one short (little endian)
        // short s = (short) ((secondByte << 8) | firstByte);
        // convert to range from -1 to (just below) 1
        return s2 / 32768.0;
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

        // Determine if mono or stereo
        int channels;     // Forget byte 23 as 99.999% of WAVs are 1 or 2 channels
        assert wav != null;
        channels = wav[22];

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
}
