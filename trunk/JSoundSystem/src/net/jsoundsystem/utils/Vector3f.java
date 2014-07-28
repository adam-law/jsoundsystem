/******************************************************************************
JSoundSystem is a simple and easy sound API to use sound in your Java applications.
Copyright (c) 2014, Johan Jansen
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list 
of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this 
list of conditions and the following disclaimer in the documentation and/or other materials 
provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used 
to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ************************************************************************/

package net.jsoundsystem.utils;

/**
 * A generic 3D floating point vector for use with 3D positional audio among other things.
 * This implementation is borrowed from the Basilisk project.
 */
public class Vector3f {
    public float x;
    public float y;
    public float z;
    
    
    /**
     * <p>Creates a new Vector3D at (0,0,0).</p>
     */
    public Vector3f() {
        this(0,0,0);
    }
    
    
    /**
     * <p>Creates a new Vector3D with the same values as the
     * specified Vector3D.</p>
     */
    public Vector3f(Vector3f v) {
        this(v.x, v.y, v.z);
    }
    
    
    /**
     * <p>Creates a new Vector3D with the specified (x, y, z) values.</p>
     */
    public Vector3f(float x, float y, float z) {
        setTo(x, y, z);
    }
    
    
    /**
     * <p>Checks if this Vector3D is equal to the specified Object.</p>
     * <p>They are equal only if the specified Object is a Vector3D
     * and the two Vector3D's x, y, and z coordinates are equal.</p>
     */
    public boolean equals(Object obj) {
        Vector3f v = (Vector3f)obj;
        return (v.x == x && v.y == y && v.z == z);
    }
    
    
    /**
     * <p>Checks if this Vector3D is equal to the specified
     * x, y, and z coordinates.</p>
     */
    public boolean equals(float x, float y, float z) {
        return (this.x == x && this.y == y && this.z == z);
    }
    
    
    /**
     * <p>Sets the vector to the same values as the specified
     * Vector3D.</p>
     */
    public void setTo(Vector3f v) {
        setTo(v.x, v.y, v.z);
    }
    
    
    /**
     * <p>Sets this vector to the specified (x, y, z) values.</p>
     */
    public void setTo(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    
    /**
     * Adds the specified (x, y, z) values to this vector.
     */
    public void add(float x, float y, float z) {
        this.x+=x;
        this.y+=y;
        this.z+=z;
    }
    
    
    /**
     * Subtracts the specified (x, y, z) values to this vector.
     */
    public void subtract(float x, float y, float z) {
        add(-x, -y, -z);
    }
    
    
    /**
     * Adds the specified vector to this vector.
     */
    public void add(Vector3f v) {
        add(v.x, v.y, v.z);
    }
    
    
    /**
     * Subtracts the specified vector from this vector.
     */
    public void subtract(Vector3f v) {
        add(-v.x, -v.y, -v.z);
    }
    
    
    /**
     * Multiplies this vector by the specified value. The new
     * length of this vector will be length()*s.
     */
    public void multiply(float s) {
        x*=s;
        y*=s;
        z*=s;
    }
    
    
    /**
     * Divides this vector by the specified value. The new
     * length of this vector will be length()/s.
     */
    public void divide(float s) {
        x/=s;
        y/=s;
        z/=s;
    }
    
    
    /**
     * Returns the length of this vector as a float.
     */
    public float length() {
        return (float)Math.sqrt(x*x + y*y + z*z);
    }
    
    
    /**
     * Converts this Vector3D to a unit vector, or in other
     * words, a vector of length 1. Same as calling
     * v.divide(v.length()).
     */
    public void normalize() {
        divide(length());
    }
    
    
    /**
     * Converts this Vector3D to a String representation.
     */
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
    
    
    /**
     * Rotate this vector around the x axis the specified amount,
     * using pre-computed cosine and sine values of the angle to
     * rotate.
     */
    public void rotateX(float cosAngle, float sinAngle) {
        float newY = y*cosAngle - z*sinAngle;
        float newZ = y*sinAngle + z*cosAngle;
        y = newY;
        z = newZ;
    }
    
    
    /**
     * Rotate this vector around the y axis the specified amount,
     * using pre-computed cosine and sine values of the angle to
     * rotate.
     */
    public void rotateY(float cosAngle, float sinAngle) {
        float newX = z*sinAngle + x*cosAngle;
        float newZ = z*cosAngle - x*sinAngle;
        x = newX;
        z = newZ;
    }
    
    
    /**
     * Rotate this vector around the y axis the specified amount,
     * using pre-computed cosine and sine values of the angle to
     * rotate.
     */
    public void rotateZ(float cosAngle, float sinAngle) {
        float newX = x*cosAngle - y*sinAngle;
        float newY = x*sinAngle + y*cosAngle;
        x = newX;
        y = newY;
    }
    
    
    /**
     * Returns the dot product of this vector and the specified
     * vector.
     */
    public float getDotProduct(Vector3f v) {
        return x*v.x + y*v.y + z*v.z;
    }
    
    
    /**
     * Sets this vector to the cross product of the two
     * specified vectors. Either of the specified vectors can
     * be this vector.
     */
    public void setToCrossProduct(Vector3f u, Vector3f v) {
        // assign to local vars first in case u or v is 'this'
        float x = u.y * v.z - u.z * v.y;
        float y = u.z * v.x - u.x * v.z;
        float z = u.x * v.y - u.y * v.x;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    
    /**
     * Gets the distance squared between this vector and the
     * specified vector.
     */
    public float getDistanceSq(Vector3f v) {
        float dx = v.x - x;
        float dy = v.y - y;
        float dz = v.z - z;
        return dx*dx + dy*dy + dz*dz;
    }
    
    
    /**
     * Gets the distance between this vector and the
     * specified vector.
     */
    public float getDistance(Vector3f v) {
        return (float)Math.sqrt(getDistanceSq(v));
    }
    
    
    /**
     * Sets the length of this Vector3D
     */
    public void setLength(float newLength) {
        normalize();
        multiply(newLength);
    }
}
