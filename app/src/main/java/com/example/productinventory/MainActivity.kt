package com.example.productinventory

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import com.example.productinventory.ui.theme.ProductInventoryTheme
import androidx.compose.ui.platform.LocalContext
import com.example.productinventory.model.Product
import com.example.productinventory.model.Stock
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import com.google.gson.Gson


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProductInventoryApp()
        }
    }
}

@Composable
fun ProductInventoryApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("productList") { ProductListScreen(navController) }
        composable("productDetail/{productJson}") { backStackEntry ->
            val productJson = backStackEntry.arguments?.getString("productJson") ?: ""
            ProductDetailScreen(navController, productJson)
        }
        composable("statistics") { StatisticsScreen(navController) }
    }
}

@Composable
fun ProductListScreen(navController: NavHostController) {
    val products = Stock.products // Obtém a lista de produtos
    val totalValue = Stock.calculateTotalValue() // Calcula o valor total do estoque
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Text("Lista de Produtos", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Valor Total do Estoque: R$ $totalValue",
                style = MaterialTheme.typography.bodyMedium
            )

            LazyColumn {
                items(products.size) { index ->
                    val product = products[index]
                    ProductItem(product, navController)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                navController.navigate("statistics") // Navegar para a tela de estatísticas
            }) {
                Text("Estatísticas")
            }
        }
    }
}

@Composable
fun StatisticsScreen(navController: NavHostController) {
    val totalValue = Stock.calculateTotalValue() // Calcula o valor total do estoque
    val totalQuantity =
        Stock.products.sumOf { it.quantity } // Calcula a quantidade total de produtos



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start

        ) {
            Text("Estatísticas do Estoque", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Valor Total do Estoque: R$ $totalValue",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Quantidade Total de Produtos: $totalQuantity",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Voltar")
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("${product.name} (${product.quantity} unidades)")

        Button(onClick = {
            // Serializa o produto em JSON
            val productJson = Gson().toJson(product)
            navController.navigate("productDetail/$productJson") // Navega para a tela de detalhes
        }) {
            Text("Detalhes")
        }
    }
}

@Composable
fun ProductDetailScreen(navController: NavHostController, productJson: String) {
    // Deserializa o objeto Product do JSON
    val product = Gson().fromJson(productJson, Product::class.java)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Detalhes dos produtos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            Text("Nome: ${product.name}")
            Text("Categoria: ${product.category}")
            Text("Preço: R$ ${product.price}")
            Text("Quantidade em Estoque: ${product.quantity}")

            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Voltar")
            }
        }
    }


}

@Composable
fun MainScreen(navController: NavHostController) {


    val context = LocalContext.current
    var name = remember { mutableStateOf("") }
    var category = remember { mutableStateOf("") }
    var price = remember { mutableStateOf("") }
    var quantity = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Text("Inventário de Produtos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Nome do Produto") }
            )
            TextField(
                value = category.value,
                onValueChange = { category.value = it },
                label = { Text("Categoria") }
            )
            TextField(
                value = price.value,
                onValueChange = { price.value = it },
                label = { Text("Preço") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            TextField(
                value = quantity.value,
                onValueChange = { quantity.value = it },
                label = { Text("Quantidade em Estoque") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, // Espaço igual entre os botões
                modifier = Modifier.fillMaxWidth() // Preencher a largura disponível
            ) {
                Button(onClick = {
                    val priceValue = price.value.toDoubleOrNull()
                    val quantityValue = quantity.value.toIntOrNull()

                    // Validações dos campos
                    if (name.value.isBlank() || category.value.isBlank() || priceValue == null || quantityValue == null || (quantityValue < 1 && priceValue < 0)) {
                        Toast.makeText(
                            context,
                            "Todos os campos são obrigatórios e devem ser válidos!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (quantityValue < 1) {
                        Toast.makeText(
                            context,
                            "A quantidade deve ser maior ou igual a 1!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (priceValue < 0) {
                        Toast.makeText(
                            context,
                            "O preço não pode ser negativo!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Stock.addProduct(
                            Product(
                                name.value,
                                category.value,
                                priceValue,
                                quantityValue
                            )
                        )
                        Toast.makeText(
                            context,
                            "Produto cadastrado com sucesso!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }) {
                    Text("Cadastrar")
                }

                Button(onClick = {
                    navController.navigate("productList") // Navegar para a lista de produtos
                }) {
                    Text("Ver Produtos")
                }

            }

        }
    }
}