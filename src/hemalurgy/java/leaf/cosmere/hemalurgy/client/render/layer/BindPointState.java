/*
 * Decompiled with CFR 0.152.
 */
package leaf.cosmere.hemalurgy.client.render.layer;

import java.util.HashSet;
import java.util.Set;

public final class BindPointState {
    public static boolean active = false;
    public static Set<String> current = Set.of();
    public static final Set<String> BACK_KEYS = Set.of("linchpin:1", "mental:1", "mental:2", "mental:3", "mental:4", "temporal:3", "temporal:4", "temporal:5", "temporal:6");

    private BindPointState() {
    }

    public static void setFor(Set<String> all, Set<String> equipped, boolean back) {
        active = true;
        HashSet<String> keys = new HashSet<String>(all);
        keys.removeAll(equipped);
        if (back) {
            keys.retainAll(BACK_KEYS);
        } else {
            keys.removeAll(BACK_KEYS);
        }
        current = keys;
    }

    public static void clear() {
        active = false;
        current = Set.of();
    }
}
