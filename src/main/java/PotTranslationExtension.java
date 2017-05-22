/*
 * Copyright 2017, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.ast.impl.BlockImpl;
import org.asciidoctor.extension.Treeprocessor;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.catalog.parse.MessageStreamParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Asccidoctorj extension which inserts the translated contents of a Po file
 * into an asciidoc file.
 *
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class PotTranslationExtension extends Treeprocessor {

    @Override
    public Document process(Document document) {

        String pofile = (String)config.get("pofile");

        // Parse the pot file
        Map<String, Message> messages = new HashMap<>();

        MessageStreamParser parser = null;
        try {
            parser = new MessageStreamParser(new File(pofile));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        while(parser.hasNext()) {
            Message mssg = parser.next();
            messages.put(mssg.getMsgid(), mssg);
        }

        translateNode(document, messages);

        return document;
    }

    private void translateNode(StructuralNode node, Map<String, Message> translations) {
        if(node.getContent() != null) {
            String content = node.getContent().toString();
            Message mssg = translations.get(content);

            if(mssg != null) {
                // Different node types, need different handling
                if (node instanceof BlockImpl) {
                    ((BlockImpl) node).setSource(mssg.getMsgstr());
                    ((BlockImpl) node).setSource(mssg.getMsgstr());
                }
                // TODO Add more node types to this conditional
            }
        }

        if(node.getBlocks() != null) {
            node.getBlocks().forEach(
                    structuralNode -> translateNode(structuralNode,
                            translations));
        }
    }
}
