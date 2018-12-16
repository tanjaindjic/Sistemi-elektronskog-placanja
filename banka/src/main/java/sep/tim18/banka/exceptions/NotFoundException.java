package sep.tim18.banka.exceptions;

public class NotFoundException extends Exception{
    private static final long serialVersionUID = 1L;

    public NotFoundException() {
        super("Pogresni podaci, nije moguce naci zeljenu transakciju.");
    }
}
