import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PanelTablero extends JPanel {

    // Colores del tablero
    private static final Color COLOR_FONDO =
            new Color(20, 22, 27);

    private static final Color COLOR_VACIO =
            new Color(30, 33, 39);

    private static final Color COLOR_REJILLA =
            new Color(86, 91, 101);

    private static final int MARGEN_CELDA = 2;

    private final Color[] coloresJugadores = {
            new Color(66, 165, 245),
            new Color(239, 83, 80),
            new Color(102, 187, 106),
            new Color(255, 202, 40),
            new Color(171, 71, 188),
            new Color(38, 198, 190)
    };

    private final int filas;
    private final int columnas;

    private char[][] celdas;

    // Crea un tablero con tamaño predeterminado
    public PanelTablero() {

        this(15, 20);
    }

    // Prepara el tablero
    public PanelTablero(
            int filas,
            int columnas) {

        if (filas <= 0 || columnas <= 0) {
            throw new IllegalArgumentException(
                    "Las dimensiones del tablero deben ser positivas."
            );
        }

        this.filas = filas;
        this.columnas = columnas;

        celdas =
                new char[filas][columnas];

        inicializarCeldas();
        setBackground(COLOR_FONDO);
        setPreferredSize(new Dimension(400, 300));
    }

    // Actualiza el tablero recibido
    public void actualizarTablero(
            String tablero) {

        actualizarDesdeTexto(tablero);
    }

    // Lee el texto y actualiza las celdas
    public void actualizarDesdeTexto(
            String tableroTexto) {

        char[][] nuevasCeldas =
                new char[filas][columnas];

        for (int fila = 0; fila < filas; fila++) {

            for (int columna = 0; columna < columnas; columna++) {

                nuevasCeldas[fila][columna] = '.';
            }
        }

        if (tableroTexto != null) {

            String[] lineas =
                    tableroTexto.split("\\R");

            int filaTablero = 0;

            for (String linea : lineas) {

                if (filaTablero >= filas) {
                    break;
                }

                String contenido =
                        linea.trim();

                if (
                        contenido.length() < 2
                                || !contenido.startsWith("|")
                                || !contenido.endsWith("|")
                                || contenido.substring(
                                        1,
                                        contenido.length() - 1
                                ).contains("|")
                ) {

                    continue;
                }

                String celdasLinea =
                        contenido.substring(
                                1,
                                contenido.length() - 1
                        ).trim();

                String[] valores =
                        celdasLinea.isEmpty()
                                ? new String[0]
                                : celdasLinea.split("\\s+");

                boolean lineaValida = true;

                for (String valor : valores) {

                    if (valor.length() != 1) {

                        lineaValida = false;
                        break;
                    }
                }

                if (!lineaValida) {

                    continue;
                }

                for (
                        int columna = 0;
                        columna < columnas
                                && columna < valores.length;
                        columna++
                ) {

                    nuevasCeldas[filaTablero][columna] =
                            valores[columna].charAt(0);
                }

                filaTablero++;
            }
        }

        synchronized (this) {

            celdas = nuevasCeldas;
        }

        repaint();
    }

    // Dibuja el tablero
    @Override
    protected void paintComponent(
            Graphics graphics) {

        super.paintComponent(graphics);

        Graphics2D graphics2D =
                (Graphics2D) graphics.create();

        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int anchoCelda =
                getWidth() / columnas;

        int altoCelda =
                getHeight() / filas;

        char[][] copia;

        synchronized (this) {

            copia = new char[filas][columnas];

            for (int fila = 0; fila < filas; fila++) {

                System.arraycopy(
                        celdas[fila],
                        0,
                        copia[fila],
                        0,
                        columnas
                );
            }
        }

        graphics2D.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        Math.max(12, Math.min(anchoCelda, altoCelda) / 2)
                )
        );

        FontMetrics metricas =
                graphics2D.getFontMetrics();

        for (int fila = 0; fila < filas; fila++) {

            for (int columna = 0; columna < columnas; columna++) {

                int x =
                        columna * anchoCelda;

                int y =
                        fila * altoCelda;

                int xCelda =
                        x + MARGEN_CELDA;

                int yCelda =
                        y + MARGEN_CELDA;

                int anchoBloque =
                        Math.max(
                                0,
                                anchoCelda - MARGEN_CELDA * 2
                        );

                int altoBloque =
                        Math.max(
                                0,
                                altoCelda - MARGEN_CELDA * 2
                        );

                char valor =
                        copia[fila][columna];

                graphics2D.setColor(
                        colorCelda(valor)
                );

                graphics2D.fillRoundRect(
                        xCelda,
                        yCelda,
                        anchoBloque,
                        altoBloque,
                        5,
                        5
                );

                graphics2D.setColor(COLOR_REJILLA);
                graphics2D.drawRoundRect(
                        xCelda,
                        yCelda,
                        anchoBloque,
                        altoBloque,
                        5,
                        5
                );

                if (valor != '.') {

                    String letra =
                            String.valueOf(valor);

                    int textoX =
                            x
                                    + (anchoCelda
                                    - metricas.stringWidth(letra)) / 2;

                    int textoY =
                            y
                                    + (altoCelda
                                    - metricas.getHeight()) / 2
                                    + metricas.getAscent();

                    graphics2D.setColor(Color.WHITE);
                    graphics2D.drawString(
                            letra,
                            textoX,
                            textoY
                    );
                }
            }
        }

        graphics2D.dispose();
    }

    // Elige el color de cada celda
    private Color colorCelda(
            char valor) {

        if (valor == '.') {
            return COLOR_VACIO;
        }

        if (valor >= 'A' && valor <= 'F') {

            return coloresJugadores[valor - 'A'];
        }

        return Color.GRAY;
    }

    // Deja vacías las celdas
    private void inicializarCeldas() {

        for (int fila = 0; fila < filas; fila++) {

            for (int columna = 0; columna < columnas; columna++) {

                celdas[fila][columna] = '.';
            }
        }
    }
}
