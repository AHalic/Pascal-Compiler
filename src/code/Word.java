package code;

public final class Word {

    private byte[] bytes;

    public static Word fromInt(int value) {
        Word word = new Word();
        word.bytes = new byte[] {
                        (byte)(value >>> 24),
                        (byte)(value >>> 16),
                        (byte)(value >>> 8 ),
                        (byte)(value >>> 0 )
                       };
        return word;
    }
    
    public static Word fromFloat(float value) {
        int intBits = Float.floatToIntBits(value);
        return fromInt(intBits);
    }
    
    public int toInt() {
        return ((bytes[0] & 0xFF) << 24) | 
               ((bytes[1] & 0xFF) << 16) | 
               ((bytes[2] & 0xFF) << 8 ) | 
               ((bytes[3] & 0xFF) << 0 );
    }
    
    public float toFloat() {
        int intBits = this.toInt();
        return Float.intBitsToFloat(intBits);
    }
    
}
