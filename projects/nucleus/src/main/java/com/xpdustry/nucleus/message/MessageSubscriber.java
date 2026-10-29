// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.message;

@FunctionalInterface
public interface MessageSubscriber<E> {

    void onMessage(String sender, E message);
}
