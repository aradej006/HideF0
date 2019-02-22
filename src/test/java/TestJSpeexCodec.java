/******************************************************************************
 *                                                                            *
 * Copyright (c) 1999-2004 Wimba S.A., All Rights Reserved.                   *
 *                                                                            *
 * COPYRIGHT:                                                                 *
 *      This software is the property of Wimba S.A.                           *
 *      This software is redistributed under the Xiph.org variant of          *
 *      the BSD license.                                                      *
 *      Redistribution and use in source and binary forms, with or without    *
 *      modification, are permitted provided that the following conditions    *
 *      are met:                                                              *
 *      - Redistributions of source code must retain the above copyright      *
 *      notice, this list of conditions and the following disclaimer.         *
 *      - Redistributions in binary form must reproduce the above copyright   *
 *      notice, this list of conditions and the following disclaimer in the   *
 *      documentation and/or other materials provided with the distribution.  *
 *      - Neither the name of Wimba, the Xiph.org Foundation nor the names of *
 *      its contributors may be used to endorse or promote products derived   *
 *      from this software without specific prior written permission.         *
 *                                                                            *
 * WARRANTIES:                                                                *
 *      This software is made available by the authors in the hope            *
 *      that it will be useful, but without any warranty.                     *
 *      Wimba S.A. is not liable for any consequence related to the           *
 *      use of the provided software.                                         *
 *                                                                            *
 * Class: TestJSpeexCodec.java                                                *
 *                                                                            *
 * Author: Marc GIMPEL                                                        *
 *                                                                            *
 * Date: 21st October 2004                                                    *
 *                                                                            *
 ******************************************************************************/

/* $Id$ */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jetbrains.annotations.NotNull;
import org.xiph.speex.WaveToolbox;
import pl.pw.radeja.JSpeexDec;
import pl.pw.radeja.JSpeexEnc;
import pl.pw.radeja.speex.encoders.HideF0EncoderFirstLast;

import java.io.File;
import java.io.IOException;

/**
 * JUnit Tests for JSpeex.
 * <p/>
 * Tests Encoding and decoding of various Wave files (silence, 440 Hz Sine wave
 * and Gaussian White Noise) using the command line encoder.
 *
 * @author Marc Gimpel, Wimba S.A. (mgimpel@horizonwimba.com)
 * @version $Revision$
 */
public class TestJSpeexCodec
        extends TestCase {
    /**
     * Directory where audio generated by the tests are outputed.
     */
    public static final String AUDIO_OUTPUT_DIRECTORY = "temp" + File.separator;

    /**
     * Constructor
     *
     * @param arg0
     */
    public TestJSpeexCodec(String arg0) {
        super(arg0);
    }

    /**
     * Command line entrance.
     *
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestJSpeexCodec.suite());
    }

    ///////////////////////////////////////////////////////////////////////////
    // TestCase classes to override
    ///////////////////////////////////////////////////////////////////////////

    /**
     *
     */
    protected void setUp() {
    }

    /**
     *
     */
    protected void tearDown() {
    }

    /**
     *
     */
//  protected void runTest()
//  {
//  }

    /**
     * Builds the Test Suite.
     *
     * @return the Test Suite.
     */
    @NotNull
    public static Test suite() {
        return new TestSuite(TestJSpeexCodec.class);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Tests
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Tests encoding and decoding of 8kHz Mono Audio Signals.
     */
    public void test8kHzMono() {
        encodeDecodeSine("sin8m", 0, 8000, 1, false);
        encodeDecodeSine("sin8mv", 0, 8000, 1, true);
        encodeDecodeNoise("gwn8m", 0, 8000, 1, false);
        encodeDecodeNoise("gwn8mv", 0, 8000, 1, true);
        encodeDecodeSilence("blank8m", 0, 8000, 1, false);
        encodeDecodeSilence("blank8mv", 0, 8000, 1, true);
    }

    /**
     * Tests encoding and decoding of 11kHz Mono Audio Signals.
     */
    public void test11kHzMono() {
        encodeDecodeSine("sin11m", 0, 11025, 1, false);
        encodeDecodeSine("sin11mv", 0, 11025, 1, true);
        encodeDecodeNoise("gwn11m", 0, 11025, 1, false);
        encodeDecodeNoise("gwn11mv", 0, 11025, 1, true);
        encodeDecodeSilence("blank11m", 0, 11025, 1, false);
        encodeDecodeSilence("blank11mv", 0, 11025, 1, true);
    }

    /**
     * Tests encoding and decoding of an 16kHz Mono Sine Audio Signal (440 Hz).
     */
    public void test16kHzMono() {
        encodeDecodeSine("sin16m", 1, 16000, 1, false);
        encodeDecodeSine("sin16mv", 1, 16000, 1, true);
        encodeDecodeNoise("gwn16m", 1, 16000, 1, false);
        encodeDecodeNoise("gwn16mv", 1, 16000, 1, true);
        encodeDecodeSilence("blank16m", 1, 16000, 1, false);
        encodeDecodeSilence("blank16mv", 1, 16000, 1, true);
    }

    /**
     * Tests encoding and decoding of an 22kHz Mono Sine Audio Signal (440 Hz).
     */
    public void test22kHzMono() {
        encodeDecodeSine("sin22m", 1, 22050, 1, false);
        encodeDecodeSine("sin22mv", 1, 22050, 1, true);
        encodeDecodeNoise("gwn22m", 1, 22050, 1, false);
        encodeDecodeNoise("gwn22mv", 1, 22050, 1, true);
        encodeDecodeSilence("blank22m", 1, 22050, 1, false);
        encodeDecodeSilence("blank22mv", 1, 22050, 1, true);
    }

    /**
     * Tests encoding and decoding of an 32kHz Mono Sine Audio Signal (440 Hz).
     */
    public void test32kHzMono() {
        encodeDecodeSine("sin32m", 2, 32000, 1, false);
        encodeDecodeSine("sin32mv", 2, 32000, 1, true);
        encodeDecodeNoise("gwn32m", 2, 32000, 1, false);
        encodeDecodeNoise("gwn32mv", 2, 32000, 1, true);
        encodeDecodeSilence("blank32m", 2, 32000, 1, false);
        encodeDecodeSilence("blank32mv", 2, 32000, 1, true);
    }

    /**
     * Tests encoding and decoding of an 44kHz Mono Sine Audio Signal (440 Hz).
     */
    public void test44kHzMono() {
        encodeDecodeSine("sin44m", 2, 44100, 1, false);
        encodeDecodeSine("sin44mv", 2, 44100, 1, true);
        encodeDecodeNoise("gwn44m", 2, 44100, 1, false);
        encodeDecodeNoise("gwn44mv", 2, 44100, 1, true);
        encodeDecodeSilence("blank44m", 2, 44100, 1, false);
        encodeDecodeSilence("blank44mv", 2, 44100, 1, true);
    }

    /**
     * Tests encoding and decoding of an 8kHz Stereo Sine Audio Signal (440 Hz).
     */
    public void test8kHzStereo() {
        encodeDecodeSine("sin8s", 0, 8000, 2, false);
        encodeDecodeSine("sin8sv", 0, 8000, 2, true);
        encodeDecodeNoise("gwn8s", 0, 8000, 2, false);
        encodeDecodeNoise("gwn8sv", 0, 8000, 2, true);
        encodeDecodeSilence("blank8s", 0, 8000, 2, false);
        encodeDecodeSilence("blank8sv", 0, 8000, 2, true);
    }

    /**
     * Tests encoding and decoding of an 11kHz Stereo Sine Audio Signal (440 Hz).
     */
    public void test11kHzStereo() {
        encodeDecodeSine("sin11s", 0, 11025, 2, false);
        encodeDecodeSine("sin11sv", 0, 11025, 2, true);
        encodeDecodeNoise("gwn11s", 0, 11025, 2, false);
        encodeDecodeNoise("gwn11sv", 0, 11025, 2, true);
        encodeDecodeSilence("blank11s", 0, 11025, 2, false);
        encodeDecodeSilence("blank11sv", 0, 11025, 2, true);
    }

    /**
     * Tests encoding and decoding of an 16kHz Stereo Sine Audio Signal (440 Hz).
     */
    public void test16kHzStereo() {
        encodeDecodeSine("sin16s", 1, 16000, 2, false);
        encodeDecodeSine("sin16sv", 1, 16000, 2, true);
        encodeDecodeNoise("gwn16s", 1, 16000, 2, false);
        encodeDecodeNoise("gwn16sv", 1, 16000, 2, true);
        encodeDecodeSilence("blank16s", 1, 16000, 2, false);
        encodeDecodeSilence("blank16sv", 1, 16000, 2, true);
    }

    /**
     * Tests encoding and decoding of an 22kHz Stereo Sine Audio Signal (440 Hz).
     */
    public void test22kHzStereo() {
        encodeDecodeSine("sin22s", 1, 22050, 2, false);
        encodeDecodeSine("sin22sv", 1, 22050, 2, true);
        encodeDecodeNoise("gwn22s", 1, 22050, 2, false);
        encodeDecodeNoise("gwn22sv", 1, 22050, 2, true);
        encodeDecodeSilence("blank22s", 1, 22050, 2, false);
        encodeDecodeSilence("blank22sv", 1, 22050, 2, true);
    }

    /**
     * Tests encoding and decoding of an 32kHz Stereo Sine Audio Signal (440 Hz).
     */
    public void test32kHzStereo() {
        encodeDecodeSine("sin32s", 2, 32000, 2, false);
        encodeDecodeSine("sin32sv", 2, 32000, 2, true);
        encodeDecodeNoise("gwn32s", 2, 32000, 2, false);
        encodeDecodeNoise("gwn32sv", 2, 32000, 2, true);
        encodeDecodeSilence("blank32s", 2, 32000, 2, false);
        encodeDecodeSilence("blank32sv", 2, 32000, 2, true);
    }

    /**
     * Tests encoding and decoding of an 44kHz Stereo Sine Audio Signal (440 Hz).
     */
    public void test44kHzStereo() {
        encodeDecodeSine("sin44s", 2, 44100, 2, false);
        encodeDecodeSine("sin44sv", 2, 44100, 2, true);
        encodeDecodeNoise("gwn44s", 2, 44100, 2, false);
        encodeDecodeNoise("gwn44sv", 2, 44100, 2, true);
        encodeDecodeSilence("blank44s", 2, 44100, 2, false);
        encodeDecodeSilence("blank44sv", 2, 44100, 2, true);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Build and Test Encoders and Decoders
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Tests encoding and decoding of Silence.
     *
     * @param filename
     * @param mode
     * @param sampleRate
     * @param channels
     * @param vbr
     */
    protected static void encodeDecodeSilence(String filename,
                                              final int mode,
                                              final int sampleRate,
                                              final int channels,
                                              final boolean vbr) {
        filename = AUDIO_OUTPUT_DIRECTORY + filename;
        // Setup Source
        try {
            WaveToolbox.generateSilenceWaveFile(filename + ".wav",
                    channels,
                    sampleRate,
                    5 * sampleRate);
        } catch (IOException e) {
            fail("Unable to generate input audio file");
        }
        // Encode Audio
        @NotNull JSpeexEnc enc = buildEncoder(filename, mode, sampleRate, channels, vbr);
        try {
            enc.encode();
        } catch (IOException e) {
            fail("Unable to encode audio file");
        }
        // Decode Audio
        @NotNull JSpeexDec dec = buildDecoder(filename);
        try {
            dec.decode();
        } catch (IOException e) {
            fail("Unable to decode audio file");
        }
    }

    /**
     * Tests encoding and decoding of Gaussian White Noise.
     *
     * @param filename
     * @param mode
     * @param sampleRate
     * @param channels
     * @param vbr
     */
    protected static void encodeDecodeNoise(String filename,
                                            final int mode,
                                            final int sampleRate,
                                            final int channels,
                                            final boolean vbr) {
        filename = AUDIO_OUTPUT_DIRECTORY + filename;
        // Setup Source File
        try {
            WaveToolbox.generateWhiteNoiseWaveFile(filename + ".wav",
                    channels,
                    sampleRate,
                    5 * sampleRate,
                    22000);
        } catch (IOException e) {
            fail("Unable to generate input audio file");
        }
        // Encode Audio
        @NotNull JSpeexEnc enc = buildEncoder(filename, mode, sampleRate, channels, vbr);
        try {
            enc.encode();
        } catch (IOException e) {
            fail("Unable to encode audio file");
        }
        // Decode Audio
        @NotNull JSpeexDec dec = buildDecoder(filename);
        try {
            dec.decode();
        } catch (IOException e) {
            fail("Unable to decode audio file");
        }
    }

    /**
     * Tests encoding and decoding of a Sine Audio Signal (440 Hz).
     *
     * @param filename
     * @param mode
     * @param sampleRate
     * @param channels
     * @param vbr
     */
    protected static void encodeDecodeSine(String filename,
                                           final int mode,
                                           final int sampleRate,
                                           final int channels,
                                           final boolean vbr) {
        filename = AUDIO_OUTPUT_DIRECTORY + filename;
        // Setup Source File 5s 440Hz (la)
        try {
            WaveToolbox.generateSineWaveFile(filename + ".wav",
                    channels,
                    sampleRate,
                    5 * sampleRate,
                    30000,
                    channels * sampleRate / 440);
        } catch (IOException e) {
            fail("Unable to generate input audio file");
        }
        // Encode Audio
        @NotNull JSpeexEnc enc = buildEncoder(filename, mode, sampleRate, channels, vbr);
        try {
            enc.encode();
        } catch (IOException e) {
            fail("Unable to encode audio file");
        }
        // Decode Audio
        @NotNull JSpeexDec dec = buildDecoder(filename);
        try {
            dec.decode();
        } catch (IOException e) {
            fail("Unable to decode audio file");
        }
    }

    /**
     * Build JSpeex Encoder for the given filename.
     *
     * @param filename
     * @param mode
     * @param sampleRate
     * @param channels
     * @return JSpeex Encoder for the given filename.
     */
    @NotNull
    protected static JSpeexEnc buildEncoder(final String filename,
                                            final int mode,
                                            final int sampleRate,
                                            final int channels,
                                            final boolean vbr) {
        @NotNull JSpeexEnc enc = new JSpeexEnc(new HideF0EncoderFirstLast( 0, "dump"));
        enc.setSrcFile(filename + ".wav");
        enc.setDestFile(filename + ".spx");
        enc.setSrcFormat(JSpeexEnc.FILE_FORMAT_WAVE);
        enc.setDestFormat(JSpeexEnc.FILE_FORMAT_OGG);
        enc.setPrintlevel(JSpeexEnc.ERROR);
        enc.setMode(mode); // Narrowband
        //enc.vbr_quality = 8f; // default 8
        //enc.quality = 8;      // default 8
        //enc.complexity = 3;   // default 3
        //enc.nframes = 1;      // default 1
        enc.setVbr(vbr);        // default false
        //enc.vad = false;      // default false
        //enc.dtx = false;      // default false
        enc.setSampleRate(sampleRate);
        enc.setChannels(channels);
        return enc;
    }

    /**
     * Build JSpeex Decoder for the given filename.
     *
     * @param filename filename without extention of file to decode.
     * @return JSpeex Decoder for the given filename.
     */
    @NotNull
    protected static JSpeexDec buildDecoder(final String filename) {
        @NotNull JSpeexDec dec = new JSpeexDec();
        dec.setSrcFile(filename + ".spx");
        dec.setDestFile(filename + "-encdec.wav");
        dec.setSrcFormat(JSpeexDec.FILE_FORMAT_OGG);
        dec.setDestFormat(JSpeexDec.FILE_FORMAT_WAVE);
        dec.setPrintlevel(JSpeexDec.ERROR);
        dec.setEnhanced(true);
        return dec;
    }
}
