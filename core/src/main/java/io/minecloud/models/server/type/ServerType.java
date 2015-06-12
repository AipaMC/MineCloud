/*
 * Copyright (c) 2015, Mazen Kotb <email@mazenmc.io>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package io.minecloud.models.server.type;

import io.minecloud.db.mongo.model.MongoEntity;
import io.minecloud.models.nodes.type.NodeType;
import io.minecloud.models.plugins.Plugin;
import io.minecloud.models.server.World;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import java.util.List;

@Entity(value = "server-types", noClassnameStored = true)
@EqualsAndHashCode(callSuper = true)
public class ServerType extends MongoEntity {
    @Setter
    @Id
    private String name;
    @Setter
    private int dedicatedRam;
    @Setter
    private int maxPlayers;
    @Reference
    @Setter
    private NodeType preferredNode;
    @Setter
    private String mod;
    @Setter
    private boolean defaultServer;
    @Setter
    private List<Plugin> plugins;
    @Setter
    private World defaultWorld;
    @Setter
    private List<World> worlds;

    public String name() {
        return name;
    }

    public int maxPlayers() {
        return maxPlayers;
    }

    public NodeType preferredNode() {
        return preferredNode;
    }

    public String mod() {
        return mod;
    }

    public int dedicatedRam() {
        return dedicatedRam;
    }

    public boolean defaultServer() {
        return defaultServer;
    }

    public List<Plugin> plugins() {
        return plugins;
    }

    public World defaultWorld() {
        return defaultWorld;
    }

    public List<World> worlds() {
        return worlds;
    }
}
