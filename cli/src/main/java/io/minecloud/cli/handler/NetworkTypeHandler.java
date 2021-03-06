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
package io.minecloud.cli.handler;

import asg.cliche.Command;
import asg.cliche.Param;
import io.minecloud.MineCloud;
import io.minecloud.models.bungee.Bungee;
import io.minecloud.models.bungee.type.BungeeType;
import io.minecloud.models.external.ExternalServer;
import io.minecloud.models.external.ExternalServerType;
import io.minecloud.models.network.Network;
import io.minecloud.models.network.server.ServerNetworkMetadata;
import io.minecloud.models.nodes.Node;
import io.minecloud.models.server.Server;
import io.minecloud.models.server.type.ServerType;

import java.util.*;

public class NetworkTypeHandler extends AbstractHandler {
    Network type;

    NetworkTypeHandler(String name) {
        type = MineCloud.instance().mongo()
                .repositoryBy(Network.class)
                .findFirst(name);

        if (type == null) {
            System.out.println("Could not find type in database; creating a new one...");
            type = new Network();

            type.setName(name);
        }
    }

    @Command
    public String addBungee(@Param(name = "bungee-name") String bungeeName, @Param(name = "amount") int amount) {
        BungeeType bungeeType = MineCloud.instance().mongo()
                .repositoryBy(BungeeType.class)
                .findFirst(bungeeName);

        if (bungeeType == null) {
            return "No bungees found by the name of " + bungeeName;
        }

        if (type.bungeeMetadata() == null) {
            type.setBungees(new HashMap<>());
        }

        Map<BungeeType, Integer> bungeeTypes = type.bungeeMetadata();

        bungeeTypes.put(bungeeType, amount);
        type.setBungees(bungeeTypes);

        return "Successfully added " + bungeeName + " to the Network";
    }

    @Command
    public String addServer(@Param(name = "server-name") String name, @Param(name = "min") int min, @Param(name = "max") int max) {
        ServerType serverType = MineCloud.instance().mongo()
                .repositoryBy(ServerType.class)
                .findFirst(name);

        if (serverType == null) {
            return "No server types found by the name of " + name;
        }

        if (type.serverMetadata() == null) {
            type.setServerMetadata(new ArrayList<>());
        }

        List<ServerNetworkMetadata> metadata = type.serverMetadata();
        ServerNetworkMetadata md = new ServerNetworkMetadata();

        md.setType(serverType);
        md.setMaximumAmount(max);
        md.setMinimumAmount(min);

        metadata.add(md);

        return "Successfully added " + name + " to the Network!";
    }
    
    @Command
    public String addExternalServer(@Param(name = "server-name") String name) {
        ExternalServerType serverType = MineCloud.instance().mongo()
                .repositoryBy(ExternalServerType.class)
                .findFirst(name);

        if (serverType == null) {
            return "No external server types found by the name of " + name;
        }

        if (type.externalServerTypes() == null) {
            type.setExternalServers(new ArrayList<ExternalServerType>());
        }
        
        type.externalServerTypes().add(serverType);

        return "Successfully added " + name + " to the Network!";
    }
    
    @Command
    public String removeExternalServer(@Param(name = "server-name") String name) {
        ExternalServerType serverType = MineCloud.instance().mongo()
                .repositoryBy(ExternalServerType.class)
                .findFirst(name);

        if (serverType == null) {
            return "No external server types found by the name of " + name;
        }

        if (type.externalServerTypes() == null) {
            type.setExternalServers(new ArrayList<ExternalServerType>());
        }
        
        Optional<ExternalServerType> optional = type.externalServerTypes().stream()
                .filter((st) -> st.name().equals(serverType.name()))
                .findFirst();

        if (!optional.isPresent()) {
            return name + " is not on the network!";
        }
        
        type.externalServerTypes().remove(serverType);

        return "Successfully removed " + name + " from the Network!";
    }

    @Command
    public String addNode(@Param(name = "node-name") String nodeName) {
        Node node = MineCloud.instance().mongo()
                .repositoryBy(Node.class)
                .findFirst(nodeName);

        if (node == null) {
            return "No nodes found by the name of " + nodeName;
        }

        if (type.nodes() == null) {
            type.setNodes(new ArrayList<>());
        }

        List<Node> nodes = type.nodes();

        nodes.add(node);
        type.setNodes(nodes);

        return "Successfully added " + nodeName + " to the Network";
    }

    @Command
    public String removeBungee(@Param(name = "bungee-name") String bungee) {
        BungeeType bungeeType = MineCloud.instance().mongo()
                .repositoryBy(BungeeType.class)
                .findFirst(bungee);

        if (bungeeType == null) {
            return "No bungees found by the name of " + bungee;
        }

        if (type.bungeeMetadata() == null) {
            type.setBungees(new HashMap<>());
        }

        Map<BungeeType, Integer> bungeeTypes = type.bungeeMetadata();

        if (!bungeeTypes.keySet().stream()
                .anyMatch((bt) -> bt.name().equalsIgnoreCase(bungeeType.name()))) {
            return bungee + " is not on the network";
        }

        new HashSet<>(bungeeTypes.keySet()).stream()
                .filter((bt) -> bt.name().equalsIgnoreCase(bungeeType.name()))
                .forEach(bungeeTypes::remove);
        return bungee + " has been removed from the network!";
    }

    @Command
    public String removeServer(@Param(name = "server-name") String name) {
        ServerType serverType = MineCloud.instance().mongo()
                .repositoryBy(ServerType.class)
                .findFirst(name);

        if (serverType == null) {
            return "No server types found by the name of " + name;
        }

        if (type.serverMetadata() == null) {
            type.setServerMetadata(new ArrayList<>());
        }

        List<ServerNetworkMetadata> metadata = type.serverMetadata();
        Optional<ServerNetworkMetadata> optional = metadata.stream()
                .filter((sm) -> sm.type().name().equals(serverType.name()))
                .findFirst();

        if (!optional.isPresent()) {
            return name + " is not on the network!";
        }

        metadata.remove(optional.get());
        return name + " has been removed from the network!";
    }

    @Command
    public String removeNode(@Param(name = "node-name") String nodeName) {
        Node node = MineCloud.instance().mongo()
                .repositoryBy(Node.class)
                .findFirst(nodeName);

        if (node == null) {
            return "No nodes found by the name of " + nodeName;
        }

        if (type.nodes() == null) {
            type.setNodes(new ArrayList<>());
        }

        List<Node> nodes = type.nodes();

        if (!nodes.stream()
                .anyMatch((n) -> n.name().equalsIgnoreCase(node.name()))) {
            return nodeName + " is not on the network";
        }

        new HashSet<>(nodes).stream()
        .filter((n) -> n.name().equalsIgnoreCase(node.name()))
        .forEach(nodes::remove);
        return nodeName + " has been removed from the network!";
    }
    
    @Command
    public String restartAll() {
        //Servers First
        type.servers().forEach(server -> {
            MineCloud.instance().mongo().repositoryBy(Server.class).delete(server);
        });
        //Then Bungees
        type.bungees().forEach(bungee -> {
            MineCloud.instance().mongo().repositoryBy(Bungee.class).delete(bungee);
        });
        //Then external servers (just a refresh, doesn't actually restart them)
        type.externalServers().forEach(server -> {
            MineCloud.instance().mongo().repositoryBy(ExternalServer.class).delete(server);
        });
        
        return "Restarting all servers and bungees...";
    }
    
    @Command
    public String restartServers() {
        type.servers().forEach(server -> {
            MineCloud.instance().mongo().repositoryBy(Server.class).delete(server);
        });
        //Then external servers (just a refresh, doesn't actually restart them)
        type.externalServers().forEach(server -> {
            MineCloud.instance().mongo().repositoryBy(ExternalServer.class).delete(server);
        });
        return "Restarting all servers...";
    }
    
    @Command
    public String refreshExternal() {
        type.externalServers().forEach(server -> {
            MineCloud.instance().mongo().repositoryBy(ExternalServer.class).delete(server);
        });
        return "Refreshing external servers...";
    }
    
    @Command
    public String restartBungees() {
        type.bungees().forEach(bungee -> {
            MineCloud.instance().mongo().repositoryBy(Bungee.class).delete(bungee);
        });
        return "Restarting all bungees...";
    }
    
    @Command
    public String pingCap(@Param(name = "cap") int cap) {
    	type.setPingCap(cap);
    	return "Set ping cap to: " + cap;
    }

    @Command
    public String push() {
        if (type.bungeeMetadata() == null || type.nodes() == null || type.pingCap() == -1) {
            return "Required fields (bungee, nodes, pingCap) have not been set by the user! Unable to push modifications";
        }

        MineCloud.instance().mongo().repositoryBy(Network.class).save(type);

        return "Successfully pushed modifications to database!";
    }

    @Command(name = "!show")
    public List<String> show() {
        List<String> list = new ArrayList<>();
        list.add("Currently Modeling [Network Type] (" + type.name() + ")");
        list.add("===========================================");
        list.add("Listing Specifications...");
        list.add("- Bungees: " + formatBungees(type.bungeeMetadata()));
        list.add("- Nodes: " + formatNodes(type.nodes()));
        list.add("===========================================");
        list.add("If you're ready to go, type 'push'.");
        return list;
    }

}
