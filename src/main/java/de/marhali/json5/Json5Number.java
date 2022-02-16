package de.marhali.json5;

public class Json5Number extends Json5Primitive {

    public Json5Number(Number number) {
        super(number);
    }

    @Override
    public Json5Element deepCopy() {
        return this;
    }
}
