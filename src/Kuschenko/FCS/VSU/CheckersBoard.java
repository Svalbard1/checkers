package Kuschenko.FCS.VSU;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckersBoard extends JFrame {
    private final CheckersPiece[][] board;
    private static final int SIZE = 8;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private int currentPlayer = 1;

    private boolean gameOver = false;

    public CheckersBoard() {
        board = new CheckersPiece[SIZE][SIZE];
        initializeBoard();
        initializeGUI();
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = new CheckersPiece(0);
                if ((i + j) % 2 == 0) {
                    if (i < 3) {
                        board[i][j].setColor(2);
                    } else if (i > 4) {
                        board[i][j].setColor(1);
                    }
                    board[i][j].addActionListener(new CellClickListener(i, j));
                }
            }
        }
    }

    private void initializeGUI() {
        setTitle("Шашки");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE));
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    updateCell();
                    boardPanel.add(board[i][j]);
                } else {
                    boardPanel.add(new JPanel());
                }
            }
        }

        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Перезагрузить");
        restartButton.addActionListener(e -> restartGame());
        buttonPanel.add(restartButton);

        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void restartGame() {
        dispose();
        new CheckersBoard();
    }

    private boolean canJumpAgain(int row, int col) {
        return canJumpAgainHelper(row + 2, col + 2, row + 1, col + 1) ||
                canJumpAgainHelper(row + 2, col - 2, row + 1, col - 1) ||
                canJumpAgainHelper(row - 2, col + 2, row - 1, col + 1) ||
                canJumpAgainHelper(row - 2, col - 2, row - 1, col - 1);
    }

    private boolean canJumpAgainHelper(int targetRow, int targetCol, int jumpedRow, int jumpedCol) {
        if (targetRow < 0 || targetRow >= SIZE || targetCol < 0 || targetCol >= SIZE ||
                jumpedRow < 0 || jumpedRow >= SIZE || jumpedCol < 0 || jumpedCol >= SIZE) {
            return false;
        }

        if (board[targetRow][targetCol].getColor() != 0) {
            return false;
        }

        return board[jumpedRow][jumpedCol].getColor() != 0 && board[jumpedRow][jumpedCol].getColor() != currentPlayer;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    private void handleMove(int row, int col) {
        if (gameOver) {
            return;
        }

        if (isValidMove(selectedRow, selectedCol, row, col) && board[selectedRow][selectedCol].getColor() == currentPlayer) {
            int color = board[selectedRow][selectedCol].getColor();
            boolean isKing = board[selectedRow][selectedCol].isKing();

            board[row][col].setColor(color);

            if (Math.abs(row - selectedRow) == 2) {
                int jumpedRow = (row + selectedRow) / 2;
                int jumpedCol = (col + selectedCol) / 2;
                board[jumpedRow][jumpedCol].setColor(0);
                updateCell();

                if (canJumpAgain(row, col)) {
                    board[selectedRow][selectedCol].setColor(0);
                    updateCell();
                    handleMove(row, col);
                    return;
                }
            }

            if ((color == 1 && row == 0) || (color == 2 && row == SIZE - 1)) {
                board[row][col].makeKing(true);
            } else {
                board[row][col].makeKing(isKing);
            }

            board[selectedRow][selectedCol].setColor(0);

            selectedRow = -1;
            selectedCol = -1;

            updateCell();
            updateCell();

            switchPlayer();

        } else {
            selectedRow = -1;
            selectedCol = -1;
        }
        if (isGameOver()) {
            JOptionPane.showMessageDialog(this, "Поздравляем с победой!");
            gameOver = true;
        }
    }

    private boolean isGameOver() {
        int blackCount = 0;
        int redCount = 0;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j].getColor() == 1) {
                    blackCount++;
                } else if (board[i][j].getColor() == 2) {
                    redCount++;
                }
            }
        }

        return blackCount == 0 || redCount == 0;
    }

    private boolean isValidMove(int startRow, int startCol, int targetRow, int targetCol) {
        CheckersPiece startPiece = board[startRow][startCol];
        int direction = (startPiece.getColor() == 1) ? -1 : 1; // Для RED направление вниз, для BLACK - вверх
        if (targetRow - startRow != direction && Math.abs(targetRow - startRow) != 2 && !startPiece.isKing()) {
            return false;
        }
        // Проверка, клетка пуста
        if (board[targetRow][targetCol].getColor() != 0) {
            return false;
        }
        // Проверка для боя: если шашка делает ход через одну клетку, то нужно, чтобы между
        // начальной и конечной клетками была шашка противника
        if (Math.abs(targetRow - startRow) == 2) {
            int middleRow = (startRow + targetRow) / 2;
            int middleCol = (startCol + targetCol) / 2;

            if (board[middleRow][middleCol].getColor() == 0) {
                return false;
            }

            return board[middleRow][middleCol].getColor() != startPiece.getColor();
        }
        return true;
    }

    private void updateCell() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col].setIcon(null);
            }
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int color = board[row][col].getColor();
                boolean isKing = board[row][col].isKing();
                if (color != 0) {
                    String imagePath = getImagePath(color, isKing);
                    board[row][col].setIcon(new ImageIcon(imagePath));
                }
            }
        }
    }

    private String getImagePath(int color, boolean isKing) {
        if (isKing) {
            return (color == 1) ? "Resources/kingfirst.png" : "Resources/kingsecond.png";
        } else {
            return (color == 1) ? "Resources/first.png" : "Resources/second.png";
        }
    }

    private class CellClickListener implements ActionListener {
        private final int row;
        private final int col;

        public CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedRow == -1 && selectedCol == -1) {
                if (board[row][col].getColor() != 0) {
                    selectedRow = row;
                    selectedCol = col;
                }
            } else {
                handleMove(row, col);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CheckersBoard::new);
    }
}



