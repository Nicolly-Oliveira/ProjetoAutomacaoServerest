package suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import testsAlternativos.ProdutosTest;
import testsAlternativos.UsuarioTest;
import testsJornadas.AdministracaoProdutos;
import testsJornadas.UsuarioComprador;

@Suite
@SelectClasses({
        UsuarioTest.class,
        ProdutosTest.class,
        AdministracaoProdutos.class,
        UsuarioComprador.class
})

public class SuiteRunner {

}
