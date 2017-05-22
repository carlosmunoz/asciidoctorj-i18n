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

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.extension.JavaExtensionRegistry;

import java.io.File;
import java.util.HashMap;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class TranslateAsciidocFile {

    public static void main (String ... args) throws Exception {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();

        JavaExtensionRegistry extensionRegistry = asciidoctor.javaExtensionRegistry();

        PotTranslationExtension treeprocessor = new PotTranslationExtension();
        HashMap<String, Object> config = new HashMap<>();
        config.put("pofile", "target/sample-es.po");
        treeprocessor.setConfig(config);
        extensionRegistry.treeprocessor(treeprocessor);


//            StructuredDocument structuredDocument = asciidoctor
//                    .readDocumentStructure(new BufferedReader(
//                                    new InputStreamReader(
//                                            documentUri.toURL().openStream())),
//                            ImmutableMap.of(Asciidoctor.STRUCTURE_MAX_LEVEL, 50));

        String content = asciidoctor.convertFile(new File(
                        "target/classes/sample.adoc"),
                OptionsBuilder.options().toFile(false));

        System.out.println(content);
    }
}
