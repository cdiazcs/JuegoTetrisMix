public class Pieza {

    private TipoPieza tipo;

    private int fila;
    private int columna;

    private char simbolo;

    private int[][] forma;

    public Pieza(TipoPieza tipo, char simbolo) {

        this.tipo = tipo;
        this.simbolo = simbolo;

        this.fila = 0;
        this.columna = 0;

        crearFormaInicial();
    }

    private void crearFormaInicial() {

        switch (tipo) {

            case CUADRADO:

                forma = new int[][]{
                        {1, 1},
                        {1, 1}
                };

                break;


            case T:

                forma = new int[][]{
                        {0, 1, 0},
                        {1, 1, 1}
                };

                break;


            case I:

                forma = new int[][]{
                        {1, 1, 1}
                };

                break;


            case L:

                forma = new int[][]{
                        {1, 1},
                        {1, 0}
                };

                break;


            case Y:

                forma = new int[][]{
                        {0, 1, 0},
                        {1, 1, 1},
                        {1, 0, 1}
                };

                break;
        }
    }




    public void moverAbajo() {
        fila++;
    }


    public void moverIzquierda() {
        columna--;
    }


    public void moverDerecha() {
        columna++;
    }
    //Giro horario
    public int[][] rotarHorario(){
        int filasOriginales=forma.length;
        int columnasOriginales=forma[0].length;
        int[][] nuevaForma=new int[columnasOriginales][filasOriginales];

        for (int i = 0; i < filasOriginales; i++) {
            for (int j = 0; j < columnasOriginales; j++) {
                nuevaForma[j][filasOriginales - 1 - i] = forma[i][j];
            }
        }

        return nuevaForma;
    }
    //giro antihorario
    public int[][] rotarAntihorario(){
        int filasOriginales=forma.length;
        int columnasOriginales=forma[0].length;
        int[][] nuevaForma=new int[columnasOriginales][filasOriginales];

        for (int i = 0; i < filasOriginales; i++) {
            for (int j = 0; j < columnasOriginales; j++) {
                nuevaForma[columnasOriginales - 1 - j][i] = forma[i][j];
            }
        }

        return nuevaForma;
    }
    public void aplicarForma(int[][] nuevaForma){
        this.forma=nuevaForma;
    }
     public TipoPieza getTipo() {
        return tipo;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public char getSimbolo() {
        return simbolo;
    }

    public int[][] getForma() {
        return forma;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }
}

