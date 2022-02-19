package com.texturegarden.effects.particle;

import com.texturegarden.arrays.ComplexArray2D;
import com.texturegarden.utilities.ExpandingArray;

public class ParticleManager {
  public ExpandingArray particles = new ExpandingArray();

  public void add(Particle p) {
    particles.add(p);
  }

  public void animate(ComplexArray2D c_a) {
    for (int i = particles.getSize(); --i >= 0;) {
      Particle p = (Particle) particles.get(i);
      p.animate(c_a);
    }
  }
}
