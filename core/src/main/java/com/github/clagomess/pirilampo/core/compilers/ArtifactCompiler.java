package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.fi.UIProgressFI;

public interface ArtifactCompiler {
    void setProgress(UIProgressFI progress);
    void build() throws Exception;
}
