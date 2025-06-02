/* Post Media Styles */
.post-media video {
    width: 100%;
    max-height: 400px;
    object-fit: cover;
    border-radius: 0.5rem;
}

.post-media img {
    width: 100%;
    max-height: 400px;
    object-fit: cover;
    border-radius: 0.5rem;
}

.hidden {
    display: none;
}

.container {
    max-width: 1200px;
}

/* Fixed Header - Much More Compact */
.fixed-filter-container {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    backdrop-filter: blur(10px);
    z-index: 50;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    padding: 0.75rem 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

/* Compact Header Layout */
.fixed-filter-container .max-w-4xl {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

/* Top row with export and profile */
.fixed-filter-container .flex.justify-between {
    align-items: center;
    margin-bottom: 0.25rem;
}

/* Search bar styling */
#userSearch {
    background: rgba(255, 255, 255, 0.95);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 25px;
    padding: 0.5rem 1rem 0.5rem 2.5rem;
    font-size: 0.875rem;
    transition: all 0.3s ease;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

#userSearch:focus {
    outline: none;
    box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.3);
    background: rgba(255, 255, 255, 1);
}

/* Search icon */
.fa-search {
    color: #667eea !important;
    left: 1rem !important;
}

/* Button Styles */
#exportStatisticsBtn,
.fixed-filter-container a {
    background: rgba(255, 255, 255, 0.2);
    color: white;
    border: 1px solid rgba(255, 255, 255, 0.3);
    padding: 0.5rem 1rem;
    border-radius: 20px;
    font-size: 0.875rem;
    font-weight: 500;
    transition: all 0.3s ease;
    backdrop-filter: blur(10px);
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
}

#exportStatisticsBtn:hover,
.fixed-filter-container a:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-1px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

/* Export Dropdown */
#exportDropdown {
    background: white;
    border: none;
    border-radius: 12px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    overflow: hidden;
    min-width: 100px;
}

#exportDropdown a {
    background: transparent;
    color: #374151;
    border: none;
    border-radius: 0;
    padding: 0.75rem 1rem;
    margin: 0;
    transition: background-color 0.2s ease;
}

#exportDropdown a:hover {
    background: #f8fafc;
    transform: none;
    box-shadow: none;
}

/* Sidebar - Modern Glass Effect */
.fixed.left-0 {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(15px);
    border-right: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 4px 0 20px rgba(0, 0, 0, 0.05);
    height: calc(100vh - 4rem);
    overflow-y: auto;
    top: 4rem;
    padding: 1.5rem 1rem;
}

.fixed.left-0 select {
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    padding: 0.75rem 1rem;
    font-size: 0.875rem;
    color: #374151;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.fixed.left-0 select:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.fixed.left-0 select {
    background-image: url("data:image/svg+xml;utf8,<svg fill='none' stroke='%23667eea' stroke-width='2' viewBox='0 0 24 24' xmlns='http://www.w3.org/2000/svg'><path stroke-linecap='round' stroke-linejoin='round' d='M19 9l-7 7-7-7'></path></svg>");
    background-repeat: no-repeat;
    background-position: right 1rem center;
    background-size: 1rem;
    padding-right: 2.5rem;
    appearance: none;
}

.ml-48 {
    margin-left: 12rem;
}

.pt-16 {
    padding-top: 4rem;
}

#postsContainer {
    background: transparent;
    padding: 1rem;
}


.select2-container {
    width: 100% !important;
}

.select2-selection--single {
    background: white !important;
    border: 1px solid #e5e7eb !important;
    border-radius: 12px !important;
    height: 42px !important;
    padding: 0 1rem !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05) !important;
    transition: all 0.3s ease !important;
}

.select2-selection--single:focus,
.select2-container--default.select2-container--focus .select2-selection--single {
    border-color: #667eea !important;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
}

.select2-selection__rendered {
    color: #374151 !important;
    line-height: 40px !important;
    padding: 0 !important;
}

.select2-selection__arrow {
    height: 40px !important;
    right: 1rem !important;
}

@media (max-width: 768px) {
    .fixed-filter-container {
        padding: 0.5rem 0;
    }

    .fixed-filter-container .max-w-4xl {
        padding: 0 1rem;
        gap: 0.5rem;
    }

    .fixed-filter-container .flex.justify-between {
        flex-direction: column;
        gap: 0.5rem;
        margin-bottom: 0.5rem;
    }

    .fixed.left-0 {
        position: static;
        width: 100%;
        height: auto;
        padding: 1rem;
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(15px);
        border-radius: 0;
        border-bottom: 1px solid rgba(255, 255, 255, 0.2);
        box-shadow: 0 2px 15px rgba(0, 0, 0, 0.05);
    }

    .fixed.left-0 .flex-col {
        flex-direction: row;
        flex-wrap: wrap;
        gap: 0.75rem;
    }

    .fixed.left-0 select {
        flex: 1;
        min-width: 120px;
        padding: 0.5rem 2rem 0.5rem 0.75rem;
        font-size: 0.8125rem;
    }

    .ml-48 {
        margin-left: 0;
    }

    .pt-16 {
        padding-top: 8rem;
    }

    #exportStatisticsBtn,
    .fixed-filter-container a {
        padding: 0.5rem 0.75rem;
        font-size: 0.8125rem;
    }

    #userSearch {
        width: 100%;
        min-width: auto;
    }
}

@media (max-width: 640px) {
    .pt-16 {
        padding-top: 9rem;
    }

    .fixed-filter-container .flex.justify-between {
        align-items: stretch;
    }

    #exportStatisticsBtn,
    .fixed-filter-container a {
        flex: 1;
        justify-content: center;
        text-align: center;
    }
}

html {
    scroll-behavior: smooth;
}

.fixed.left-0::-webkit-scrollbar {
    width: 4px;
}

.fixed.left-0::-webkit-scrollbar-track {
    background: transparent;
}

.fixed.left-0::-webkit-scrollbar-thumb {
    background: rgba(102, 126, 234, 0.3);
    border-radius: 2px;
}

.fixed.left-0::-webkit-scrollbar-thumb:hover {
    background: rgba(102, 126, 234, 0.5);
}