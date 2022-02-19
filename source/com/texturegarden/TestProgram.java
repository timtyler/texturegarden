package com.texturegarden;

import com.texturegarden.arrays.ComplexArray2D;
import com.texturegarden.instructions.InstructionManager;
import com.texturegarden.program.Program;
import com.texturegarden.utilities.JUR;

public class TestProgram {
  public static JUR rnd = new JUR(); // main RNG for texture - treat with care - no size-dependant operations...

  static void demoProgramSetup(Texture texture) {
    makeTestProgram(texture);
    // byte[] ba = ResourceLoader.getByteArray("", "", "a1.txt");
    // program.newProgram(ba); 
  }

  static void makeTestProgram(Texture texture) {
    texture.rain_intensity = rnd.nextInt(12);

    boolean low_frequency_quick_noise = true;
    boolean ridge_finder = false;
    boolean high_frequency_quick_noise = rnd.nextBoolean();
    boolean mediumFrequencyQuickNoise = rnd.nextBoolean();
    boolean do_sqrt = rnd.nextBoolean();
    boolean do_fft = !TG.reaction_diffusion;
    boolean add_static = (rnd.nextByte() > 96);
    boolean fractal_noise = rnd.nextBoolean();
    boolean h_smear = rnd.nextBoolean();
    boolean v_smear = rnd.nextBoolean();

    boolean warp = rnd.nextBoolean();
    int warp_iterations = (rnd.nextInt(6));

    if (!(high_frequency_quick_noise || mediumFrequencyQuickNoise)) {
      ridge_finder = (rnd.nextByte() > 32);
    }

    fractal_noise = false;
    // ridge_finder = true;

    if (fractal_noise) {
      ridge_finder = false;
      add_static = false;
      warp = false;
      warp_iterations = 1;
    }

    if (ridge_finder) {
      high_frequency_quick_noise = false;
      mediumFrequencyQuickNoise = false;
    }

    // generate warp field...
    texture.c_a2D[0] = new ComplexArray2D(texture.width, texture.height);

    Program program = texture.program;

    if (do_fft) {
      if (fractal_noise) {
        program.addInstruction(InstructionManager.FREQUENCY_FRACTAL_NOISE_2D);
        program.addData(0x1FF);
        program.addData(0x04);
      } else {
        if (low_frequency_quick_noise) {
          program.addInstruction(InstructionManager.BANDWIDTH_LIMITED_FREQUENCY_NOISE_2D);
          program.addData(0xFF);
          int radius = (rnd.nextInt() & 15) + 3;
          program.addData(radius - 3);
          program.addData(radius);
        }

        if (mediumFrequencyQuickNoise) {
          program.addInstruction(InstructionManager.FREQUENCY_NOISE_2D_QUICK);
          program.addData(0xFFFF);
          program.addData(rnd.nextInt() & 31);
          program.addData(rnd.nextInt() & 31);
          program.addData((rnd.nextInt() & 15) + 2);
          program.addData((rnd.nextInt() & 15) + 2);
        }

        if (high_frequency_quick_noise) {
          program.addInstruction(InstructionManager.FREQUENCY_NOISE_2D_QUICK);
          program.addData(0xFFFF);
          program.addData(rnd.nextInt() & 127);
          program.addData(rnd.nextInt() & 127);
          program.addData((rnd.nextInt() & 15) + 2);
          program.addData((rnd.nextInt() & 15) + 2);
        }
      }

      program.addInstruction(InstructionManager.INVERSE_FFT_2D);

      // c_a2D[0] = FFT2D.fft(c_a2D[0]); // not inverse?

      // one method is to get square roots first...
      if (do_sqrt) {
        program.addInstruction(InstructionManager.SQUARE_ROOT_2D);
      }
      //program.addInstruction(InstructionManager.PROCESS_2D);
      //program.addInstruction(InstructionManager.PROCESS_2D);
      //program.addInstruction(InstructionManager.ADD_CEILING);
      //program.addInstruction(InstructionManager.X);
      // program.addInstruction(InstructionManager.Y);
      //program.addData(0x0);
    }

    if (h_smear) {
      // H
      program.addInstruction(InstructionManager.NEW_BUFFER_1D);
      program.addInstruction(InstructionManager.BUFFER_HEIGHT);
      program.addInstruction(InstructionManager.ZERO_R_1D);
      program.addInstruction(InstructionManager.ZERO_I_1D);
      program.addInstruction(InstructionManager.FREQUENCY_NOISE_1D);
      program.addData(0x1FFF);
      program.addData(0x0C);
      program.addInstruction(InstructionManager.INVERSE_FFT_1D);
      program.addInstruction(InstructionManager.EQUALISE_SCALED_1D);
      program.addData(0x1FF);

      program.addInstruction(InstructionManager.SMEAR_H);
      program.addInstruction(InstructionManager.ADD_CEILING);
      program.addInstruction(InstructionManager.C);
      program.addInstruction(InstructionManager.E);
    }

    if (v_smear) {
      // V
      program.addInstruction(InstructionManager.NEW_BUFFER_1D);
      program.addInstruction(InstructionManager.BUFFER_WIDTH);
      program.addInstruction(InstructionManager.ZERO_R_1D);
      program.addInstruction(InstructionManager.ZERO_I_1D);
      program.addInstruction(InstructionManager.FREQUENCY_NOISE_1D);
      program.addData(0x1FFF);
      program.addData(0x0C);
      program.addInstruction(InstructionManager.INVERSE_FFT_1D);
      program.addInstruction(InstructionManager.EQUALISE_SCALED_1D);
      program.addData(0x1FF);

      program.addInstruction(InstructionManager.SMEAR_V);
      program.addInstruction(InstructionManager.ADD_CEILING);
      program.addInstruction(InstructionManager.C);
      program.addInstruction(InstructionManager.E);
    }

    if (warp) {
      for (int i = 0; i < warp_iterations; i++) {
        // selected_buffer_1d();
        // FrequencyNoise1D
        program.addInstruction(InstructionManager.NEW_BUFFER_1D);
        program.addInstruction(InstructionManager.BUFFER_WIDTH);
        program.addInstruction(InstructionManager.FREQUENCY_NOISE_1D);
        program.addData(0x00FFFFFF);
        program.addData(0x0C);
        program.addInstruction(InstructionManager.INVERSE_FFT_1D);
        program.addInstruction(InstructionManager.EQUALISE_SCALED_1D);
        program.addData(0x1000);
        program.addInstruction(InstructionManager.WAVES_V);

        // V
        program.addInstruction(InstructionManager.NEW_BUFFER_1D);
        program.addInstruction(InstructionManager.BUFFER_HEIGHT);
        program.addInstruction(InstructionManager.FREQUENCY_NOISE_1D);
        program.addData(0x00FFFFFF);
        program.addData(0x0C);
        program.addInstruction(InstructionManager.INVERSE_FFT_1D);
        program.addInstruction(InstructionManager.EQUALISE_SCALED_1D);
        program.addData(0x1000);
        program.addInstruction(InstructionManager.WAVES_H);
      }
    }

    if (ridge_finder) {
      program.addInstruction(InstructionManager.RIDGE_FINDER_2D);
      program.addInstruction(InstructionManager.BLUR_2D);
    }

    if (add_static) {
      program.addInstruction(InstructionManager.PROCESS_2D);
      program.addInstruction(InstructionManager.ADD_CEILING);
      program.addInstruction(InstructionManager.C);
      program.addInstruction(InstructionManager.RANDOM);
      program.addData(0x400);
    }

    program.addInstruction(InstructionManager.EQUALISE_2D);

    //if (do_concertina) {
    //program.addInstruction(InstructionManager.CONCERTINA_2D);
    //program.addData(concertina_iterations << 8);
    //}

    program.addInstruction(InstructionManager.RETURN);
  }
}
