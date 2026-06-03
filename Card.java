public class Card {
    private String text;
    private CardType type;
    private boolean isRevealed;

    public Card(String text, CardType type) {
        this.text = text;
        this.type = type;
        this.isRevealed = false;
    }

    // Bilgileri dışarıdan okumak ve değiştirmek için gerekli fonksiyonlar
    public String getText() { return text; }
    public CardType getType() { return type; }
    public boolean isRevealed() { return isRevealed; }
    public void reveal() { this.isRevealed = true; }
}