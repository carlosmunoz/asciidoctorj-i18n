/*
 * Copyright 2016, Red Hat, Inc. and individual contributors as indicated by the
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

import org.asciidoctor.ast.DescriptionList;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.ast.Table;
import org.asciidoctor.extension.Treeprocessor;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Asciidoctorj extension which extracts a POT file with the translatable contents
 * for an asciidoc file
 *
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class PotExtractionExtension extends Treeprocessor {
    private Document document;

    private BufferedWriter buffWriter;
    private PoWriter potWriter;

    public Document process(Document document) {

        // Configuration parameter for the target pot file
        String potfile = (String)config.get("potfile");

        this.document = document;
        try {
            this.potWriter = new PoWriter(false);
            this.buffWriter = Files.newBufferedWriter(Paths.get(potfile), Charset.forName("UTF-8"));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        final List<StructuralNode> blocks = this.document.getBlocks();

        for (int i = 0; i < blocks.size(); i++) {
            final StructuralNode currentBlock = blocks.get(i);
            processBlock(currentBlock);
        }

        try {
            this.buffWriter.flush();
            this.buffWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return this.document;
    }

    /**
     * Process a Structural node (part of the asciidoc AST) and tries to
     * extract translatable content from it.
     * @param block
     */
    private void processBlock(StructuralNode block) {
        // Custom node types need different processing. This might need more
        // cases
        if(block instanceof Table) {
            Table table = (Table)block;
            processTable(table);
        } else if(block instanceof org.asciidoctor.ast.List) {
            org.asciidoctor.ast.List list = (org.asciidoctor.ast.List)block;
            processList(list);
        } else if(block instanceof DescriptionList) {
            DescriptionList descList = (DescriptionList) block;
            processDescriptionList(descList);
        } else if(block.getBlocks() != null ) {
            if (block.getBlocks().size() == 0) {
                writePotEntry((String)block.getContent());
            } else {
                block.getBlocks().forEach(b -> processBlock(b));
            }
        }
    }

    private void writePotEntry(String source) {
        if(source == null || source.isEmpty()) {
            return;
        }

        try {
            Message newMssg = new Message();
            newMssg.setMsgid(source);
            newMssg.setMsgstr("");
            potWriter.write(newMssg, buffWriter);
            buffWriter.newLine();
            buffWriter.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processTable(Table table) {
        table.getHeader().forEach(row -> row.getCells().forEach(cell -> {
            writePotEntry(cell.getSource());
        }));

        table.getBody().forEach(row ->  row.getCells().forEach(cell -> {
                writePotEntry(cell.getSource());
            }
        ));
    }

    private void processList(org.asciidoctor.ast.List list) {
        list.getItems().forEach(item -> {
            writePotEntry((String)item.getContent());
        });
    }

    private void processDescriptionList(DescriptionList descriptionList) {
        descriptionList.getItems().forEach(descriptionListEntry -> {
            descriptionListEntry.getTerms().forEach(listItem -> {
                writePotEntry(listItem.getSource());
            });
            writePotEntry(descriptionListEntry.getDescription().getSource());
        });
    }
}
