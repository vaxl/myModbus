package base;

public interface Database {
    void create();
    byte [] read(int reg,int num,RegistrsTypes type) ;
    void update (int reg,int num,RegistrsTypes type) ;
}
