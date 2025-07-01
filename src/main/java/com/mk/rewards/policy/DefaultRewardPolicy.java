package com.mk.rewards.policy;

public class DefaultRewardPolicy implements RewardPolicy {

    @Override
    public int calculate(double amount) {
        if (amount <= 50) {
            return 0;
        } else if (amount <= 100) {
            return (int) (amount - 50); // 1 point per $ over 50
        } else {
            return (int) (2 * (amount - 100) + 50); // 2 points per $ over 100 + 1 per $ between 50–100
        }
    }
}
