public class Tablero {

    private int filas;
    private int columnas;

    private char[][] celdas;

    // Crea el tablero
    public Tablero(int filas, int columnas) {

        this.filas = filas;
        this.columnas = columnas;

        celdas = new char[filas][columnas];

        inicializar();
    }

    // Deja vacías las celdas
    private void inicializar() {

        for (int i = 0; i < filas; i++) {

            for (int j = 0; j < columnas; j++) {

                celdas[i][j] = '.';
            }
        }
    }

    // Muestra el tablero
    public void imprimir() {

        System.out.println();

        for (int i = 0; i < filas; i++) {

            System.out.print("| ");

            for (int j = 0; j < columnas; j++) {

                System.out.print(celdas[i][j] + " ");
            }

            System.out.println("|");
        }

        System.out.println();
    }

    // Muestra el tablero con la pieza
    public void imprimir(Pieza pieza) {

        System.out.println();

        for (int i = 0; i < filas; i++) {

            System.out.print("| ");

            for (int j = 0; j < columnas; j++) {

                char valor = celdas[i][j];

                int[][] forma = pieza.getForma();

                for (int x = 0; x < forma.length; x++) {

                    for (int y = 0; y < forma[x].length; y++) {

                        if (forma[x][y] == 1) {

                            int filaPieza =
                                    pieza.getFila() + x;

                            int columnaPieza =
                                    pieza.getColumna() + y;

                            if (
                                    filaPieza == i &&
                                    columnaPieza == j
                            ) {

                                valor =
                                        pieza.getSimbolo();
                            }
                        }
                    }
                }

                System.out.print(valor + " ");
            }

            System.out.println("|");
        }

        System.out.println();
    }

    // Comprueba si la pieza cabe
    public boolean posicionValida(
            Pieza pieza,
            int nuevaFila,
            int nuevaColumna) {

        return posicionValida(
                pieza.getForma(),
                nuevaFila,
                nuevaColumna
        );
    }

    public boolean posicionValida(
            int[][] forma,
            int nuevaFila,
            int nuevaColumna) {

        for (int i = 0; i < forma.length; i++) {

            for (int j = 0; j < forma[i].length; j++) {

                if (forma[i][j] == 1) {

                    int filaTablero =
                            nuevaFila + i;

                    int columnaTablero =
                            nuevaColumna + j;

                    // Comprueba los límites
                    if (
                            filaTablero < 0 ||
                            filaTablero >= filas ||
                            columnaTablero < 0 ||
                            columnaTablero >= columnas
                    ) {

                        return false;
                    }

                    // Evita chocar con otras piezas
                    if (
                            celdas[filaTablero][columnaTablero]
                                    != '.'
                    ) {

                        return false;
                    }
                }
            }
        }

        return true;
    }

    // Fija la pieza en el tablero
    public void fijarPieza(Pieza pieza) {

        int[][] forma =
                pieza.getForma();

        for (int i = 0; i < forma.length; i++) {

            for (int j = 0; j < forma[i].length; j++) {

                if (forma[i][j] == 1) {

                    int filaTablero =
                            pieza.getFila() + i;

                    int columnaTablero =
                            pieza.getColumna() + j;

                    celdas[filaTablero][columnaTablero] =
                            pieza.getSimbolo();
                }
            }
        }
    }

    // Revisa si la fila está llena
    private boolean filaCompleta(int fila) {

        for (int j = 0; j < columnas; j++) {

            if (celdas[fila][j] == '.') {

                return false;
            }
        }

        return true;
    }

    // Baja las filas superiores
    private void eliminarFila(int fila) {

        for (int i = fila; i > 0; i--) {

            for (int j = 0; j < columnas; j++) {

                celdas[i][j] =
                        celdas[i - 1][j];
            }
        }

        for (int j = 0; j < columnas; j++) {

            celdas[0][j] = '.';
        }
    }

    // Elimina las filas completas
    public int eliminarLineasCompletas() {

        int lineasEliminadas = 0;

        // Revisa las filas desde abajo
        for (int i = filas - 1; i >= 0; i--) {

            if (filaCompleta(i)) {

                eliminarFila(i);

                lineasEliminadas++;

                // Revisa la fila que bajó
                i++;
            }
        }

        return lineasEliminadas;
    }
    // Convierte el tablero a texto
    public String comoTexto() {

    StringBuilder sb =
            new StringBuilder();

    for (int i = 0; i < filas; i++) {

        sb.append("| ");

        for (int j = 0; j < columnas; j++) {

            sb.append(
                    celdas[i][j]
            );

            sb.append(" ");
        }

        sb.append("|\n");
    }

    return sb.toString();
}
// Devuelve el ancho del tablero
public int getColumnas() {

    return columnas;
}
// Devuelve una copia de las celdas
public char[][] copiaCeldas() {

    char[][] copia =
            new char[filas][columnas];

    for (int i = 0; i < filas; i++) {

        for (int j = 0; j < columnas; j++) {

            copia[i][j] =
                    celdas[i][j];
        }
    }

    return copia;
}
}