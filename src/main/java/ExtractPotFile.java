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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.ast.ContentPart;
import org.asciidoctor.ast.StructuredDocument;
import org.asciidoctor.extension.JavaExtensionRegistry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class ExtractPotFile {

    public static void main (String ... args) throws Exception {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();

        JavaExtensionRegistry extensionRegistry = asciidoctor.javaExtensionRegistry();

        PotExtractionExtension extension = new PotExtractionExtension();
        extension.setConfig(ImmutableMap.of("potfile", "target/sample.pot"));
        extensionRegistry.treeprocessor(extension);

        Options opts = new Options();

//        String content = asciidoctor.convertFile(new File(
//                    "target/classes/sample.adoc"),
//            opts);
        asciidoctor.loadFile(new File("target/classes/sample.adoc"), Maps.newHashMap());
    }
}
