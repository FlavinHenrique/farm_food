# Farm Food

Bem-vindo ao **Farm Food**, uma plataforma que conecta clientes diretamente a produtores rurais, oferecendo produtos frescos e de qualidade.

## 🚀 Tecnologias Utilizadas
- **Backend:** Java 17, Spring Boot, Spring Data JPA
- **Banco de Dados:** PostgreSQL (Supabase)
- **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5
- **Infraestrutura:** Docker, Render

## 📋 Pré-requisitos
Para rodar este projeto localmente, você precisará de:
- [Java Development Kit (JDK) 17](https://adoptium.net/) ou superior
- [Maven](https://maven.apache.org/) instalado e configurado no PATH, **ou** utilizar a sua própria IDE (Eclipse, IntelliJ, VS Code)
- Acesso ao banco de dados PostgreSQL (ex: Supabase)

## ⚙️ Variáveis de Ambiente
Crie um arquivo `application-local.properties` (ou configure no seu ambiente) com as seguintes chaves de acesso ao banco:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://<SEU_HOST>:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=<SEU_USUARIO>
SPRING_DATASOURCE_PASSWORD=<SUA_SENHA>
```

## 🛠️ Como Inicializar o Projeto

### Opção 1: Usando uma IDE (Recomendado)
A maneira mais fácil de rodar o projeto se você não tem o Maven no PATH do Windows:
1. Abra a pasta `farm_food` no **IntelliJ IDEA**, **Eclipse** ou **VS Code**.
2. Aguarde a IDE sincronizar as dependências do `pom.xml`.
3. Localize o arquivo principal: `src/main/java/com/foodfarmer/foodfarmer/FoodfarmerApplication.java`.
4. Clique com o botão direito e selecione **Run 'FoodfarmerApplication'**.
5. Acesse no navegador: `http://localhost:8080/`

### Opção 2: Usando o Terminal (com Maven)
Se você possui o Maven instalado:
```bash
# Baixa as dependências e roda o projeto
mvn spring-boot:run
```

### Opção 3: Usando Docker (com Docker Compose)
Se você possui o Docker Desktop instalado:
```bash
docker-compose up --build
```

## 🚢 Deploy
O projeto está configurado para deploy automatizado na plataforma **Render**. 
- O arquivo `Dockerfile` define um build em múltiplos estágios otimizado.
- O arquivo `render.yaml` descreve a infraestrutura como código (IaC).

**Lembre-se:** As variáveis de banco de dados devem ser configuradas no painel *Environment* do Render.

## 🤝 Contribuição
1. Faça um Fork do projeto
2. Crie sua Feature Branch (`git checkout -b feature/NovaFeature`)
3. Faça o Commit de suas mudanças (`git commit -m 'Add: Nova feature'`)
4. Faça o Push para a Branch (`git push origin feature/NovaFeature`)
5. Abra um Pull Request
