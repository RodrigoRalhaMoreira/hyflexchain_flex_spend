import matplotlib.pyplot as plt
import numpy as np

# Data
transaction_types = ["Type 1", "Type 2"]
consensus_mechanisms = ["PoW", "BFT"]
privacy_levels = ["Public standard", "Privacy enhanced"]

# Actual values for latency and throughput means and standard deviations
latency_data_means = {
    "PoW": [[223.268, 212.916], [47, 18.909]],  # privacy enhanced, public standard
    "BFT": [[229.193, 253.391], [0.933, 0.919]],  # privacy enhanced, public standard
}

latency_data_std = {
    "PoW": [
        [
            np.std([223.89, 233.40, 219.93, 199.45, 213.43, 223.90, 214.95, 227.67, 232.45, 243.61]),
            np.std([197.71, 213.32, 214.55, 215.41, 220.62, 225.54, 221.78, 204.54, 206.23, 209.46]),
        ],
        [
            np.std([43.91, 37.72, 53.70, 40.80, 40.77, 51.68, 58.11, 47.21, 36.88, 59.22]),
            np.std([20.52, 29.12, 14.71, 23.99, 17.08, 16.17, 14.98, 18.30, 17.79, 16.43]),
        ],
    ],
    "BFT": [
        [
            np.std([257.59, 299.42, 217.42, 215.53, 217.06, 217.79, 218.42, 213.14, 213.64, 221.92]),
            np.std([244.22, 255.17, 271.20, 242.84, 240.75, 260.07, 266.02, 252.62, 247.03, 253.99]),
        ],
        [
            np.std([0.93, 0.92, 0.95, 0.96, 0.92, 0.92, 0.93, 0.93, 0.94, 0.93]),
            np.std([0.93, 0.91, 0.91, 0.92, 0.91, 0.92, 0.94, 0.92, 0.91, 0.92]),
        ],
    ],
}

throughput_data_means = {
    "PoW": [[6.7, 6.92], [21.75, 38.32]],  # Privacy enhanced, Public standard
    "BFT": [[6.41, 5.66], [49.28, 49.31]],  # Privacy enhanced, Public standard
}

throughput_data_std = {
    "PoW": [
        [
            np.std([6.7, 6.6, 6.9, 7.3, 6.8, 6.6, 6.8, 6.4, 6.6, 6.3]),
            np.std([7.4, 7.0, 6.3, 6.9, 6.8, 6.7, 6.7, 7.1, 7.2, 7.1]),
        ],
        [
            np.std([21.4, 24.7, 20.0, 21.3, 22.2, 19.7, 20.2, 23.3, 25.0, 19.7]),
            np.std([33.7, 37.4, 38.7, 39.0, 36.9, 41.8, 36.4, 38.1, 38.1, 43.1]),
        ],
    ],
    "BFT": [
        [
            np.std([5.6, 5.1, 6.6, 6.7, 6.6, 6.7, 6.6, 6.8, 6.8, 6.6]),
            np.std([5.8, 5.6, 5.4, 5.8, 5.9, 5.4, 5.4, 5.7, 6.0, 5.6]),
        ],
        [
            np.std([49.2, 49.3, 49.3, 49.2, 49.2, 49.3, 49.3, 49.4, 49.3, 49.3]),
            np.std([49.3, 49.3, 49.2, 49.3, 49.3, 49.3, 49.4, 49.3, 49.3, 49.4]),
        ],
    ],
}

# Plot settings
bar_width = 0.35  # width of the bars
index = np.arange(len(transaction_types))
hatches = ["xx", ".."]

# Colors for digital display
colors = ["#f4a261", "#a2d2ff"]  # These are blue and orange, can be adjusted

# Save each graph as a separate PNG with print-friendly design
for i, consensus in enumerate(consensus_mechanisms):
    fig, ax = plt.subplots(figsize=(7, 5))

    # Latency with error bars, hatches for differentiation, and colors
    bars1 = ax.bar(
        index,
        latency_data_means[consensus][0],
        bar_width,
        yerr=latency_data_std[consensus][0],
        capsize=5,
        color=colors[0],
        hatch=hatches[0],
        label="Privacy enhanced",
    )
    bars2 = ax.bar(
        index + bar_width,
        latency_data_means[consensus][1],
        bar_width,
        yerr=latency_data_std[consensus][1],
        capsize=5,
        color=colors[1],
        hatch=hatches[1],
        label="Public standard",
    )

    ax.set_xlabel("Transaction Type", fontsize=16)
    ax.set_ylabel("Latency (s)", fontsize=16)
    ax.set_xticks(index + bar_width / 2)
    ax.set_xticklabels(transaction_types, fontsize=14)
    ax.set_yticklabels([0, 50, 100, 150, 200, 250], fontsize=14)
    ax.legend(fontsize=10, loc="upper right")

    plt.tight_layout()
    plt.savefig(f"./{consensus}_latency_color_bw.png")
    plt.close()

    fig, ax = plt.subplots(figsize=(7, 5))

    # Throughput with error bars, hatches for differentiation, and colors
    bars1 = ax.bar(
        index,
        throughput_data_means[consensus][0],
        bar_width,
        yerr=throughput_data_std[consensus][0],
        capsize=5,
        color=colors[0],
        hatch=hatches[0],
        label="Privacy enhanced",
    )
    bars2 = ax.bar(
        index + bar_width,
        throughput_data_means[consensus][1],
        bar_width,
        yerr=throughput_data_std[consensus][1],
        capsize=5,
        color=colors[1],
        hatch=hatches[1],
        label="Public standard",
    )
    values = [0, 10, 20, 30, 40, 50] if consensus == "BFT" else [0, 5, 10, 15, 20, 25, 30, 35, 40, 45]
    ax.set_xlabel("Transaction Type", fontsize=16)
    ax.set_ylabel("Throughput (tx/sec)", fontsize=16)
    ax.set_xticks(index + bar_width / 2)
    ax.set_xticklabels(transaction_types, fontsize=14)
    ax.set_yticklabels(values, fontsize=14)
    ax.legend(fontsize=10, loc="upper left")

    plt.tight_layout()
    plt.savefig(f"./{consensus}_throughput_color_bw.png")
    plt.close()
