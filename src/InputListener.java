public interface InputListener {

    void handle(Input input);

    enum Input{
        up_a, up_b,
        down_a, down_b,
        left_a, left_b,
        right_a, right_b
    }

}
