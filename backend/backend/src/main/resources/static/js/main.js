// 🔽 Toggle category dropdown visibility
function toggleCategory() {
  const categoryBox = document.getElementById("category-box");
  categoryBox.classList.toggle("hidden");
}

// 🔽 Handle category selection
function selectCategory(category) {
  const categoryLabel = document.getElementById("selected-category");
  const uploadBox = document.getElementById("upload-box");

  if (categoryLabel) {
    categoryLabel.textContent = `Selected: ${category}`;
    categoryLabel.style.display = "block";
  }

  uploadBox.classList.remove("hidden");
  console.log("Selected Category:", category);
}

// 🔽 Navigate to insights drive page
function goToDrive() {
  window.location.href = "drive.html";
}

let latestChartValues = [0, 0];
let chartInstance = null;

document.addEventListener("DOMContentLoaded", () => {
  const uploadBtn = document.querySelector(".upload-btn");
  if (!uploadBtn) return;

  uploadBtn.addEventListener("click", async () => {
    const file1 = document.getElementById("file1").files[0];
    const file2 = document.getElementById("file2").files[0];
    const insightBox = document.getElementById("insight-box");
    const chartCanvas = document.getElementById("insight-chart");

    insightBox.innerHTML = "";
    insightBox.classList.add("hidden");
    chartCanvas.classList.add("hidden");

    if (!file1 || !file2) {
      alert("❗ Please upload both files before submitting.");
      return;
    }

    const formData = new FormData();
    formData.append("file1", file1);
    formData.append("file2", file2);

    try {
      const uploadResponse = await fetch("http://localhost:8080/upload", {
        method: "POST",
        body: formData,
      });

      const uploadText = await uploadResponse.text();

      if (!uploadResponse.ok || uploadText.toLowerCase().includes("error")) {
        console.error("Upload Error:", uploadText);
        alert("❌ Upload Failed: " + uploadText);
        return;
      }

      const analyzeResponse = await fetch("http://localhost:5000/analyze");
      if (!analyzeResponse.ok) throw new Error("Flask analysis error.");

      const analysis = await analyzeResponse.json();

      const {
        increased_units = [],
        increased_revenue = [],
        decreased_revenue = [],
        summary = { file1_total: 0, file2_total: 0, percent_change: 0, status: "increased" }
      } = analysis;

      latestChartValues = [summary.file1_total, summary.file2_total];

      let content = `<h3>📊 Insight Summary</h3>`;
      content += `
        <ul>
          <li><strong>File 1 Total:</strong> ${summary.file1_total}</li>
          <li><strong>File 2 Total:</strong> ${summary.file2_total}</li>
          <li><strong>Change:</strong> ${summary.status} by ${summary.percent_change}%</li>
        </ul>
      `;

      if (increased_units.length) {
        content += `<h4>📈 Increased Unit Sales</h4><ul>`;
        increased_units.forEach(item => {
          content += `<li><strong>${item.product}</strong>: +${item.increase_amount} units (from ${item.previous_total} to ${item.current_total})</li>`;
        });
        content += `</ul>`;
      }

      if (increased_revenue.length) {
        content += `<h4>💰 Increased Revenue</h4><ul>`;
        increased_revenue.forEach(item => {
          content += `<li><strong>${item.product}</strong>: +₹${item.increase_amount} (from ₹${item.previous_total} to ₹${item.current_total})</li>`;
        });
        content += `</ul>`;
      }

      if (decreased_revenue.length) {
        content += `<h4>📉 Decreased Revenue</h4><ul>`;
        decreased_revenue.forEach(item => {
          content += `<li><strong>${item.product}</strong>: -₹${item.decrease_amount} (from ₹${item.previous_total} to ₹${item.current_total})</li>`;
        });
        content += `</ul>`;
      }

      content += `
        <button id="show-chart-btn">Show Insight Chart</button>
        <canvas id="insight-chart" class="hidden" style="max-width: 600px; margin-top: 20px;"></canvas>
      `;

      insightBox.innerHTML = content;
      insightBox.classList.remove("hidden");

      const showChartBtn = document.getElementById("show-chart-btn");
      if (showChartBtn) {
        showChartBtn.addEventListener("click", () => {
          const ctx = document.getElementById("insight-chart").getContext("2d");
          document.getElementById("insight-chart").classList.remove("hidden");

          if (chartInstance) chartInstance.destroy();

          chartInstance = new Chart(ctx, {
            type: "bar",
            data: {
              labels: ["File 1", "File 2"],
              datasets: [{
                label: "Total Comparison",
                data: latestChartValues,
                backgroundColor: ["#4CAF50", "#2196F3"]
              }]
            },
            options: {
              responsive: true,
              plugins: {
                title: {
                  display: true,
                  text: "📊 Total Value Comparison"
                },
                legend: { display: false }
              },
              scales: {
                y: {
                  beginAtZero: true
                }
              }
            }
          });
        });
      }

    } catch (err) {
      console.error("❌ Process Error:", err);
      alert("❌ Something went wrong during file upload or analysis.");
    }
  });
});
